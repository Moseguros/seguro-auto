# MoSeguros — app móvel

App Flutter (Android e iOS) para clientes finais. Ficha de origem: `docs/tasks/S0-06.md`.

## Requisitos

- Flutter na versão fixada em `pubspec.yaml` (`environment.flutter`).
- Node 22 (para gerar o cliente da API com `npx`) e JDK 17.

## Comandos

```bash
./tool/generate_api.sh          # gera packages/moseguros_api a partir de ../api/openapi.yaml
flutter pub get                 # também gera lib/l10n/app_localizations*.dart
flutter analyze
flutter test
flutter build apk --flavor dev -t lib/main_dev.dart --debug
```

## Flavors

| Flavor | Ponto de entrada | Application id | Nome | API |
| --- | --- | --- | --- | --- |
| `dev` | `lib/main_dev.dart` | `mz.co.moseguros.app.dev` | MoSeguros Dev | `http://10.0.2.2:8080` (backend local visto do emulador) |
| `staging` | `lib/main_staging.dart` | `mz.co.moseguros.app.staging` | MoSeguros Staging | `--dart-define=API_BASE_URL=https://…` |
| `prod` | `lib/main_prod.dart` | `mz.co.moseguros.app` | MoSeguros | `--dart-define=API_BASE_URL=https://…` |

O HTTP sem TLS só é permitido no flavor `dev`, para `10.0.2.2` e `localhost`
(`android/app/src/dev/res/xml/network_security_config.xml`).

No iOS há os schemes `dev`, `staging` e `prod` com as configurações `Debug-<flavor>`, `Release-<flavor>` e `Profile-<flavor>`.

## Correr num emulador Android

```bash
flutter run --flavor dev -t lib/main_dev.dart
flutter test integration_test/smoke_test.dart --flavor dev
```

## Estrutura

- `lib/core/` — `Env`, `ApiClient` (Dio, timeout de 15 s), router, tema da marca, `CtaButton`.
- `lib/features/<feature>/` — ecrãs e providers Riverpod de cada funcionalidade.
- `lib/l10n/` — textos (`app_pt.arb` por defeito, `app_en.arb`).
- `packages/moseguros_api/` — cliente gerado; **não editar à mão**.
- `assets/brand/` — cópias de `docs/brand/`; `assets/icon/` — ícone provisório.
