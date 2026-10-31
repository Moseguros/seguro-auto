# Marca MoSeguros

**Conceito:** Deep Blue + Orange — segurança e dinamismo.
**Personalidade:** comercial, energética e confiante.

## Logótipo

| Ficheiro | Uso |
| --- | --- |
| `logo.svg` | Horizontal, fundos claros (web, PDFs) |
| `logo-white.svg` | Horizontal, fundos escuros (barra superior escura, modo escuro) |
| `logo-mark.svg` | Só o símbolo: favicon, ícone da app, avatar |

O logótipo actual é **provisório**. Quando chegar o definitivo, substituem-se só estes três ficheiros (e as cópias geradas em `web/public/brand/` e no ícone da app).

## Cores

| Token | Claro | Escuro | Uso |
| --- | --- | --- | --- |
| `primary` | `#142D4E` | `#9DB8D9` | Barra superior, títulos, botões principais, links |
| `onPrimary` | `#FFFFFF` | `#0E1A2B` | Texto sobre `primary` |
| `accent` (`secondary`) | `#F28C28` | `#F28C28` | **Só** acções de conversão (ver abaixo) e destaques pontuais |
| `onAccent` | `#142D4E` | `#0E1A2B` | Texto sobre laranja — **nunca branco** |
| `background` | `#E8EFF6` | `#0E1A2B` | Fundo das páginas |
| `surface` | `#FFFFFF` | `#16263B` | Cartões, painéis, diálogos |
| `textPrimary` | `#142D4E` | `#E8EFF6` | Texto principal |
| `textSecondary` | `#4A5D75` | `#9DB8D9` | Texto secundário |
| `error` | `#C62828` | `#EF9A9A` | Erros |
| `success` | `#2E7D32` | `#81C784` | Confirmações |

### Contraste (WCAG 2.1)

| Combinação | Rácio | Resultado |
| --- | --- | --- |
| Branco sobre `#142D4E` | 13,9 : 1 | AAA |
| `#142D4E` sobre `#F28C28` | 5,7 : 1 | AA |
| Branco sobre `#F28C28` | 2,5 : 1 | **Reprovado** — não usar |
| `#142D4E` sobre `#E8EFF6` | 12,0 : 1 | AAA |
| `#F28C28` sobre `#E8EFF6` (como texto) | 2,1 : 1 | **Reprovado** — laranja nunca como cor de texto em fundo claro |
| `#F28C28` sobre `#0E1A2B` | 7,1 : 1 | AAA |

## Regras de uso do laranja

O laranja chama a atenção para as acções que geram negócio. Só se usa em:

- **Simular seguro**
- **Comparar propostas**
- **Adquirir apólice**
- **Renovar seguro**

Na web, botão `variant="cta"`; na app, componente `CtaButton`. No máximo um botão laranja visível por ecrã; as restantes acções usam o botão primário azul ou botões de contorno. O laranja também pode marcar um estado "recomendado" numa comparação de cotações.

## Tipografia

`Inter` (Google Fonts), com fallback `"Segoe UI", Roboto, Arial, sans-serif`. Títulos 600–700; texto 400; números de valores com algarismos tabulares (`font-variant-numeric: tabular-nums`).

## Forma

Cantos `8 px`; cartões sem sombra forte (elevação 1); espaçamento em múltiplos de 8.
