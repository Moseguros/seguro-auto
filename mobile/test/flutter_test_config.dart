import 'dart:async';

import 'package:drift/drift.dart';

/// Corre antes de cada ficheiro de testes: cada teste de widgets cria a sua base
/// drift em memória, por isso o aviso de bases repetidas não se aplica.
Future<void> testExecutable(FutureOr<void> Function() testMain) async {
  driftRuntimeOptions.dontWarnAboutMultipleDatabases = true;
  await testMain();
}
