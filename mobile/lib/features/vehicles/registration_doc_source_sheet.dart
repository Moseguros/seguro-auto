import 'package:flutter/material.dart';

import '../../l10n/app_localizations.dart';

/// De onde vem o livrete.
enum RegistrationDocSource { camera, gallery, pdf }

/// Folha com "Tirar foto", "Escolher da galeria" e "Escolher PDF".
Future<RegistrationDocSource?> showRegistrationDocSourceSheet(
  BuildContext context,
) => showModalBottomSheet<RegistrationDocSource>(
  context: context,
  showDragHandle: true,
  builder: (context) => const RegistrationDocSourceSheet(),
);

class RegistrationDocSourceSheet extends StatelessWidget {
  const RegistrationDocSourceSheet({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    Widget option(RegistrationDocSource source, IconData icon, String label) =>
        ListTile(
          key: Key('doc-source-${source.name}'),
          leading: Icon(icon),
          title: Text(label),
          onTap: () => Navigator.of(context).pop(source),
        );
    return SafeArea(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          option(
            RegistrationDocSource.camera,
            Icons.photo_camera_outlined,
            l10n.docSourceCamera,
          ),
          option(
            RegistrationDocSource.gallery,
            Icons.photo_library_outlined,
            l10n.docSourceGallery,
          ),
          option(
            RegistrationDocSource.pdf,
            Icons.picture_as_pdf_outlined,
            l10n.docSourcePdf,
          ),
          const SizedBox(height: 8),
        ],
      ),
    );
  }
}
