import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import 'quote_draft.dart';
import 'quote_rules.dart';

/// Rascunho do assistente, em memória enquanto a app está aberta; limpo quando a
/// simulação é criada (S3-05 §2).
class QuoteDraftController extends Notifier<QuoteDraft> {
  @override
  QuoteDraft build() => QuoteDraft.empty;

  /// Escolhe o veículo e o condutor por defeito (o habitual do veículo; senão o
  /// condutor que é o próprio cliente; senão nenhum).
  void selectVehicle(String vehicleId, {String? defaultDriverId}) {
    if (state.vehicleId == vehicleId) return;
    state = state.copyWith(
      vehicleId: () => vehicleId,
      driverId: () => defaultDriverId,
      fieldErrors: _without(['vehicleId', 'driverId']),
    );
  }

  void selectDriver(String? driverId) => state = state.copyWith(
    driverId: () => driverId,
    fieldErrors: _without(['driverId']),
  );

  /// Mudar de tipo volta a pôr as coberturas por defeito; fora de `TEMPORARY` a
  /// duração é sempre 1 ano.
  void selectType(String type) {
    if (state.productType == type) return;
    final durations = durationsFor(type);
    final duration = state.durationDays;
    state = state.copyWith(
      productType: () => type,
      coverages: () => null,
      durationDays: () => type == QuoteProductTypes.temporary
          ? (duration != null && durations.contains(duration) ? duration : null)
          : oneYearDays,
      fieldErrors: _without(['productType', 'coverages', 'durationDays']),
    );
  }

  void setCoverages(Map<String, int?> coverages) => state = state.copyWith(
    coverages: () => Map.unmodifiable(coverages),
    fieldErrors: _without(['coverages']),
  );

  void setStartDate(DateTime date) => state = state.copyWith(
    startDate: () => DateTime(date.year, date.month, date.day),
    fieldErrors: _without(['startDate']),
  );

  void setDuration(int days) => state = state.copyWith(
    durationDays: () => days,
    fieldErrors: _without(['durationDays']),
  );

  /// Erros de campo do `createQuoteRequest`.
  void setFieldErrors(Map<String, String> errors) =>
      state = state.copyWith(fieldErrors: Map.unmodifiable(errors));

  void reset() => state = QuoteDraft.empty;

  /// `?redo=`: os mesmos dados de um pedido anterior. Se a data de início já
  /// passou, passa a [today] e devolve `true` (aviso no ecrã).
  bool loadFrom(QuoteRequestResponse request, DateTime today) {
    final start = request.startDate.toDateTime();
    final moved = start.isBefore(today);
    state = QuoteDraft(
      vehicleId: request.vehicle.vehicleId,
      driverId: request.driver.driverId,
      productType: request.productType.name,
      coverages: Map.unmodifiable({
        for (final coverage in request.requestedCoverages)
          coverage.code.name: coverage.capital?.amount,
      }),
      startDate: moved ? today : start,
      durationDays: request.durationDays,
    );
    return moved;
  }

  Map<String, String> _without(List<String> prefixes) => {
    for (final entry in state.fieldErrors.entries)
      if (!prefixes.any(entry.key.startsWith)) entry.key: entry.value,
  };
}

final quoteDraftControllerProvider =
    NotifierProvider<QuoteDraftController, QuoteDraft>(
      QuoteDraftController.new,
    );
