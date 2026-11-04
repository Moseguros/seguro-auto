import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Relógio da app: hora das fotografias da vistoria e "hoje" das datas da
/// proposta. Os testes fixam-no.
final clockProvider = Provider<DateTime Function()>((ref) => DateTime.now);
