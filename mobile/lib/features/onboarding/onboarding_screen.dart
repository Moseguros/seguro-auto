import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../l10n/app_localizations.dart';
import 'onboarding_controller.dart';

class _Page {
  const _Page(this.icon, this.title, this.text);

  final IconData icon;
  final String Function(AppLocalizations) title;
  final String Function(AppLocalizations) text;
}

final _pages = <_Page>[
  _Page(
    Icons.compare_arrows,
    (l) => l.onboarding1Title,
    (l) => l.onboarding1Text,
  ),
  _Page(Icons.bolt, (l) => l.onboarding2Title, (l) => l.onboarding2Text),
  _Page(Icons.shield, (l) => l.onboarding3Title, (l) => l.onboarding3Text),
];

/// `/onboarding`: três ecrãs de apresentação na primeira abertura.
class OnboardingScreen extends ConsumerStatefulWidget {
  const OnboardingScreen({super.key});

  @override
  ConsumerState<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends ConsumerState<OnboardingScreen> {
  final _controller = PageController();
  int _page = 0;

  bool get _isLast => _page == _pages.length - 1;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  // O router segue para o login quando a flag fica gravada.
  void _finish() => ref.read(onboardingControllerProvider.notifier).complete();

  void _next() => _controller.nextPage(
    duration: const Duration(milliseconds: 300),
    curve: Curves.easeInOut,
  );

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Align(
              alignment: AlignmentDirectional.centerEnd,
              child: Padding(
                padding: const EdgeInsets.all(8),
                child: _isLast
                    ? const SizedBox(height: 48)
                    : TextButton(
                        onPressed: _finish,
                        child: Text(l10n.onboardingSkip),
                      ),
              ),
            ),
            Expanded(
              child: PageView(
                controller: _controller,
                onPageChanged: (page) => setState(() => _page = page),
                children: [
                  for (final page in _pages)
                    SingleChildScrollView(
                      padding: const EdgeInsets.symmetric(horizontal: 24),
                      child: Column(
                        children: [
                          const SizedBox(height: 32),
                          Icon(
                            page.icon,
                            size: 120,
                            color: theme.colorScheme.primary,
                          ),
                          const SizedBox(height: 32),
                          Text(
                            page.title(l10n),
                            textAlign: TextAlign.center,
                            style: theme.textTheme.headlineSmall?.copyWith(
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                          const SizedBox(height: 16),
                          Text(
                            page.text(l10n),
                            textAlign: TextAlign.center,
                            style: theme.textTheme.bodyLarge?.copyWith(
                              color: theme.colorScheme.onSurfaceVariant,
                            ),
                          ),
                        ],
                      ),
                    ),
                ],
              ),
            ),
            _PageIndicator(count: _pages.length, current: _page),
            Padding(
              padding: const EdgeInsets.all(16),
              child: SizedBox(
                width: double.infinity,
                child: FilledButton(
                  style: FilledButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: _isLast ? _finish : _next,
                  child: Text(
                    _isLast ? l10n.onboardingStart : l10n.onboardingNext,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _PageIndicator extends StatelessWidget {
  const _PageIndicator({required this.count, required this.current});

  final int count;
  final int current;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Semantics(
      label: AppLocalizations.of(context).onboardingPage(current + 1, count),
      excludeSemantics: true,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          for (var i = 0; i < count; i++)
            AnimatedContainer(
              duration: const Duration(milliseconds: 200),
              margin: const EdgeInsets.symmetric(horizontal: 4),
              width: i == current ? 24 : 8,
              height: 8,
              decoration: BoxDecoration(
                color: i == current
                    ? scheme.primary
                    : scheme.onSurfaceVariant.withValues(alpha: 0.3),
                borderRadius: BorderRadius.circular(4),
              ),
            ),
        ],
      ),
    );
  }
}
