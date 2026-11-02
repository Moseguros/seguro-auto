import 'app.dart';
import 'core/config/env.dart';

/// Flavor prod: `--dart-define=API_BASE_URL=https://…` obrigatório.
void main() => runMoSeguros(Env.fromDefine('prod'));
