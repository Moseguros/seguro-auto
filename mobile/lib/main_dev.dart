import 'app.dart';
import 'core/config/env.dart';

/// Flavor dev: backend local visto do emulador Android (10.0.2.2 = anfitrião).
void main() =>
    runMoSeguros(const Env(apiBaseUrl: 'http://10.0.2.2:8080', name: 'dev'));
