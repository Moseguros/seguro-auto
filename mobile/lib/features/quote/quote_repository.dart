import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';

/// Pedidos de cotação do cliente (tag `Rating`) e catálogo público (tag `Catalog`).
class QuoteRepository {
  QuoteRepository(this._ref);

  final Ref _ref;

  ApiClient get _api => _ref.read(apiClientProvider);

  /// `createQuoteRequest`. As listas de cotações voltam a ser lidas.
  Future<QuoteRequestResponse> create(CreateQuoteRequest request) async {
    final response = (await _api.rating.createQuoteRequest(
      createQuoteRequest: request,
    )).data!;
    _ref
      ..invalidate(recentQuotesProvider)
      ..invalidate(myQuotesControllerProvider);
    return response;
  }

  Future<QuoteRequestResponse> get(String requestId) async =>
      (await _api.rating.getQuoteRequest(id: requestId)).data!;
}

final quoteRepositoryProvider = Provider<QuoteRepository>(QuoteRepository.new);

/// Chave do catálogo: categoria e uso do veículo.
typedef CatalogKey = ({String category, String usage});

/// Produtos publicados para a categoria e o uso (`listCatalogProducts`); o tipo
/// filtra-se no `buildCoverageOptions`.
final catalogProductsProvider = FutureProvider.autoDispose
    .family<List<CatalogProductResponse>, CatalogKey>(
      (ref, key) async =>
          (await ref
                  .watch(apiClientProvider)
                  .catalog
                  .listCatalogProducts(
                    vehicleCategory: VehicleCategory.valueOf(key.category),
                    usage: VehicleUsage.valueOf(key.usage),
                    size: 100,
                  ))
              .data!
              .content
              .toList(),
      retry: (_, _) => null,
    );

/// Um pedido com as cotações (`getQuoteRequest`).
final quoteRequestProvider = FutureProvider.autoDispose
    .family<QuoteRequestResponse, String>(
      (ref, id) async =>
          (await ref.watch(apiClientProvider).rating.getQuoteRequest(id: id))
              .data!,
      retry: (_, _) => null,
    );

/// As 3 simulações mais recentes (`listMyQuoteRequests`, `size=3`).
final recentQuotesProvider =
    FutureProvider.autoDispose<List<QuoteRequestSummaryResponse>>(
      (ref) async =>
          (await ref
                  .watch(apiClientProvider)
                  .rating
                  .listMyQuoteRequests(size: 3, sort: 'createdAt,desc'))
              .data!
              .content
              .toList(),
      retry: (_, _) => null,
    );

/// Página carregada de "As minhas cotações".
class MyQuotesState {
  const MyQuotesState({
    required this.items,
    required this.totalElements,
    required this.nextPage,
    this.loadingMore = false,
  });

  final List<QuoteRequestSummaryResponse> items;
  final int totalElements;
  final int nextPage;
  final bool loadingMore;

  bool get hasMore => items.length < totalElements;
}

/// "As minhas cotações": `listMyQuoteRequests` com `size=20`, mais páginas ao
/// chegar ao fim da lista.
class MyQuotesController extends AsyncNotifier<MyQuotesState> {
  static const pageSize = 20;

  Future<PageResponseQuoteRequestSummaryResponse> _page(int page) async =>
      (await ref
              .read(apiClientProvider)
              .rating
              .listMyQuoteRequests(
                page: page,
                size: pageSize,
                sort: 'createdAt,desc',
              ))
          .data!;

  @override
  Future<MyQuotesState> build() async {
    final page = await _page(0);
    return MyQuotesState(
      items: page.content.toList(),
      totalElements: page.totalElements,
      nextPage: 1,
    );
  }

  /// Carrega a página seguinte (uma de cada vez).
  Future<void> loadMore() async {
    final current = state.value;
    if (current == null || current.loadingMore || !current.hasMore) return;
    state = AsyncData(
      MyQuotesState(
        items: current.items,
        totalElements: current.totalElements,
        nextPage: current.nextPage,
        loadingMore: true,
      ),
    );
    try {
      final page = await _page(current.nextPage);
      state = AsyncData(
        MyQuotesState(
          items: [...current.items, ...page.content],
          totalElements: page.totalElements,
          nextPage: current.nextPage + 1,
        ),
      );
    } on Object {
      // Fica como estava; o próximo fim da lista volta a tentar.
      state = AsyncData(current);
    }
  }
}

final myQuotesControllerProvider =
    AsyncNotifierProvider.autoDispose<MyQuotesController, MyQuotesState>(
      MyQuotesController.new,
      retry: (_, _) => null,
    );
