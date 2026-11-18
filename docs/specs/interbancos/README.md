# Pagamento por entidade e referência (rede Ponto24 da InterBancos)

Especificações e exemplos oficiais entregues pelo proprietário do produto em 18/11/2026. São a fonte de verdade do gerador de referências (S5-07) e dos leitores dos ficheiros de movimentos (S5-08). ADR-007.

| Ficheiro | O que é |
| --- | --- |
| `especificacao-ficheiro-meps.pdf` | Especificação do ficheiro de movimentos **MEPS** (InterBancos) |
| `especificacao-ficheiro-bmeps.pdf` | Especificação do ficheiro de movimentos **BMEPS** (InterBancos) |
| `exemplo-ficheiro-meps.txt` | Exemplo real MEPS (13 linhas, ASCII, CRLF) |
| `exemplo-ficheiro-bmeps.txt` | Exemplo real BMEPS (16 linhas, ASCII, CRLF) |

Os `.txt` estão com `-text` no `.gitattributes`: o Git guarda-os byte a byte, com `\r\n`. Os testes dos leitores (S5-08) usam cópias destes ficheiros e confirmam que são iguais (como o `ReferenceCasesTest` do S3-01).

O cliente paga por e-banking ou app do banco, ATM, telemóvel (`*124#`), POS ou balcão, indicando **entidade**, **referência** e **montante**. A rede valida o montante: o pagamento é sempre do montante exacto da referência. A InterBancos fecha a compensação e deposita o ficheiro de movimentos no SFTP da seguradora (empresa de serviços).

## 1. Referência de pagamento

### 1.1 Algoritmo (dado pelo proprietário do produto, confirmado nos exemplos)

Entradas:

| Campo | Dígitos | Regra |
| --- | --- | --- |
| Entidade | 5 | número de entidade da seguradora na rede |
| Número da factura | 7 | com zeros à esquerda |
| Mês | 2 | `01`–`12` |
| Montante | variável | em centavos, **sem zeros à esquerda** e sem separador (`2 000,00` → `200000`) |

Cálculo dos dígitos de controlo (`CD`):

1. `S = Entidade(5) + NúmeroFactura(7) + Mês(2) + Montante(centavos, sem zeros à esquerda)`, concatenados em texto.
2. `P = 0`; para cada dígito `d` de `S`, da esquerda para a direita: `P = ((d + P) × 10) mod 97`.
3. No fim: `Pn = (P × 10) mod 97`.
4. `CD = 98 − Pn`, sempre com 2 dígitos (`01`–`98`).

**Referência (11 dígitos) = NúmeroFactura(7) + Mês(2) + CD(2).** A entidade e o montante não aparecem na referência, mas entram no `CD`: a mesma factura e o mesmo mês com outro montante ou outra entidade dão outro `CD`.

```python
def check_digits(entity: str, invoice7: str, month2: str, amount_centavos: int) -> str:
    s = entity + invoice7 + month2 + str(amount_centavos)  # str() de um int nunca tem zeros à esquerda
    p = 0
    for ch in s:
        p = ((int(ch) + p) * 10) % 97
    return "%02d" % (98 - (p * 10) % 97)
```

### 1.2 Exemplo oficial

Entidade `54321`, factura `1212245`, mês `07`, montante `200000` (2 000,00 MT) → `CD = 02` → referência **`12122450702`**.

O formato do montante é crítico: com `"0200000"` (zeros à esquerda) o `CD` daria `21`, e a rede recusaria a referência.

### 1.3 Regras da MoSeguros sobre as entradas (decisões do proprietário do produto, 18/11/2026)

- **Número da factura:** numeração própria **por entidade** (por seguradora), sequencial a partir de `0000001`, nunca reutilizada (S5-07).
- **Mês:** o mês da emissão da referência em `Africa/Maputo`. O cliente pode pagar em qualquer altura; o mês é só parte do identificador.
- **Montante:** o montante exacto a pagar. Uma referência corresponde a um montante; se o montante mudar, gera-se uma referência nova.

### 1.4 Verificação nos exemplos

- **MEPS:** o cabeçalho traz a entidade `90002` (A033). As 11 referências do detalhe (A034) validam todas com essa entidade e o montante A011 sem zeros à esquerda. Ex.: referência `00548781108` (factura `0054878`, mês `11`, `CD` `08`), montante `0000000300000` → `"300000"`.
- **BMEPS:** o ficheiro não traz a entidade. As 14 referências validam todas com uma mesma entidade; todas as entidades de 5 dígitos que as validam são ≡ 74 (mod 97) (1 031 valores possíveis entre `00000` e `99999`). Por isso a entidade de uma seguradora BMEPS vem sempre da configuração (S5-01).
- Nos dois exemplos o mês das referências (`11`) é o mês do pagamento (Novembro de 2012) e os números de factura têm zeros à esquerda (`0054878`, `0187508`).

Casos de teste (usar nos testes do S5-07 e do simulador do S5-08):

| Entidade | Factura | Mês | Montante (centavos) | Referência | Origem |
| --- | --- | --- | --- | --- | --- |
| `54321` | `1212245` | `07` | `200000` | `12122450702` | exemplo oficial |
| `90002` | `0054878` | `11` | `300000` | `00548781108` | MEPS, linha 2 |
| `90002` | `0060460` | `11` | `300000` | `00604600879` | MEPS, linha 3 |
| `90002` | `0060462` | `11` | `275000` | `00604621032` | MEPS, linha 5 |
| `90002` | `0062040` | `11` | `744000` | `00620400869` | MEPS, linhas 7 e 8 (paga duas vezes) |
| `90002` | `0062044` | `11` | `620000` | `00620441239` | MEPS, linha 11 |
| `90002` | `0062342` | `11` | `61000` | `00623420704` | MEPS, linha 12 |
| `54321` | `1212245` | `07` | `"0200000"` (errado) | `12122450721` | **inválida** (montante com zero à esquerda) |

Para o BMEPS, os testes configuram uma entidade ≡ 74 (mod 97) — por exemplo `90090` (90090 = 97 × 928 + 74) — e confirmam as 14 referências do exemplo. O script seguinte mostra de onde vem a regra:

```python
# entidades de 5 dígitos que validam todas as referências do exemplo BMEPS
cands = [e for e in range(100000)
         if all(check_digits("%05d" % e, r[:7], r[7:9], a) == r[9:] for r, a in bmeps_refs)]
assert set(e % 97 for e in cands) == {74}
```

## 2. Ficheiro MEPS ("FICH" = MEPS)

Criado pela InterBancos no fecho da compensação; informa a empresa de serviços de todos os pagamentos do período e do total creditado. Um registo de cabeçalho (tipo `0`), registos de detalhe (tipo `2`) e um registo de rodapé (tipo `9`). ASCII. **Linhas de 107 caracteres** (sem o terminador).

O campo A007 de cada ficheiro identifica o ficheiro anterior (zeros no primeiro), para a empresa detectar ficheiros em falta. Se a conta bancária de destino mudar, a InterBancos cria ficheiros distintos, um por conta.

### 2.1 Cabeçalho (tipo `0`)

| Atributo | Posições | Comp. | Tipo | Descrição | Especificação | Exemplo |
| --- | --- | --- | --- | --- | --- | --- |
| A001 | 1–1 | 1 | A | Tipo de registo | `0` | `0` |
| A002 | 2–5 | 4 | A | Tipo de ficheiro | `MEPS` | `MEPS` |
| A003 | 6–13 | 8 | N | Id. instituição origem | `50000000` | `00000008` |
| A004 | 14–21 | 8 | N | Id. instituição destino | `00000100` | `00090002` |
| A005 | 22–30 | 9 | N | Data do processamento | `AAAAMMDDS` (S = sequência no dia) | `201211230` |
| A007 | 31–39 | 9 | N | Id. do último ficheiro | `AAAAMMDDS`; zeros no primeiro | `201211220` |
| A033 | 40–44 | 5 | N | Entidade | | `90002` |
| A006 | 45–47 | 3 | N | Código de moeda | `508` | `943` |
| A031 | 48–49 | 2 | N | Taxa de IVA | `XX` | `XX` |
| A025 | 50–53 | 4 | N | Identificação do log no F.DST | | `0000` |
| — | 54–107 | 54 | A | Filler | espaços | espaços |

### 2.2 Detalhe (tipo `2`)

| Atributo | Posições | Comp. | Tipo | Descrição | Especificação | Exemplo (linha 2) |
| --- | --- | --- | --- | --- | --- | --- |
| A001 | 1–1 | 1 | A | Tipo de registo | `2` | `2` |
| A009 | 2–3 | 2 | N | Código de processamento | `04` | `04` |
| A008 | 4–7 | 4 | N | Identificação do log InterBancos | | `0000` |
| A014 | 8–15 | 8 | N | Número do log InterBancos | | `00000000` |
| A035 | 16–27 | 12 | N | Data/hora da transacção do cliente | `AAAAMMDDHH` (a especificação diz 10 dígitos) | `201211221034` |
| A011 | 28–40 | 13 | N | Montante pago | 2 casas decimais | `0000000300000` (3 000,00) |
| A032 | 41–49 | 9 | N | Tarifa | | `000001000` (10,00) |
| A015 | 50–51 | 2 | A | Tipo de terminal | | espaços |
| A016 | 52–61 | 10 | N | Identificação do terminal | | `0000000000` |
| A017 | 62–66 | 5 | N | Identificação da transacção | | `00000` |
| A030 | 67–81 | 15 | A | Localidade do terminal | | espaços |
| A034 | 82–92 | 11 | N | Referência do pagamento | | `00548781108` |
| A010 | 93–93 | 1 | N | Modo de envio da comunicação | zeros nas entidades sem notificação de pagamentos | `0` |
| A012 | 94–94 | 1 | N | Código de resposta da empresa | | `0` |
| A013 | 95–106 | 12 | A | Nr. de identificação da resposta | | espaços |
| — | 107–107 | 1 | A | Filler | | espaço |

### 2.3 Rodapé (tipo `9`)

| Atributo | Posições | Comp. | Tipo | Descrição | Especificação | Exemplo |
| --- | --- | --- | --- | --- | --- | --- |
| A001 | 1–1 | 1 | A | Tipo de registo | `9` | `9` |
| A024 | 2–9 | 8 | N | Número de registos de detalhe | | `00000011` |
| A026 | 10–26 | 17 | N | Montante total das transacções | 2 casas decimais | `00000000005132000` (51 320,00) |
| A027 | 27–38 | 12 | N | Total da tarifação | | `000000011000` (110,00) |
| A023 | 39–50 | 12 | N | IVA | 2 casas decimais | `00000000000 ` (desalinhado) |
| — | 51–107 | 57 | A | Filler | | espaços |

## 3. Ficheiro BMEPS ("FICH" = BMEPS)

Criado pela InterBancos no fecho da compensação; **um ficheiro por perfil da empresa**. O primeiro ficheiro tem a sequência `00001`, e cada ficheiro novo soma 1. Nome: `BMEPS_outEEEPP508AAMMDDhhmmss` (`EEE` ID da empresa, `PP` ID do perfil, `508` código da moeda, data e hora da geração). ASCII. **Cabeçalho com 19 caracteres, detalhe com 91 (com espaços no fim), rodapé com 40.**

### 3.1 Cabeçalho (tipo `0`)

| Campo | Posições | Comp. | Tipo | Especificação | Exemplo |
| --- | --- | --- | --- | --- | --- |
| Tipo de registo | 1–1 | 1 | A | `0` | `0` |
| ID da empresa | 2–4 | 3 | N | | `800` |
| ID do perfil | 5–6 | 2 | N | | `02` |
| Data de criação do ficheiro | 7–14 | 8 | N | `DDMMAAAA` | `22112012` |
| ID do ficheiro | 15–19 | 5 | N | sequencial | `00001` |

### 3.2 Detalhe (tipo `1`)

| Campo | Posições | Comp. | Tipo | Especificação | Exemplo (linha 2) |
| --- | --- | --- | --- | --- | --- |
| Tipo de registo | 1–1 | 1 | A | `1` | `1` |
| Referência | 2–12 | 11 | N | | `01875081156` |
| Valor | 13–28 | 16 | N | 2 casas decimais | `0000000000572000` (5 720,00) |
| Comissão | 29–44 | 16 | N | 2 casas decimais | `0000000000000500` (5,00) |
| Data | 45–52 | 8 | N | `DDMMAAAA` | `20112012` |
| Hora | 53–58 | 6 | N | `hhmmss` | `185300` |
| ID da transacção | 59–65 | 7 | N | | `0000000` |
| ID do distribuidor/terminal | 66–75 | 10 | A | | espaços |
| Localização do distribuidor/terminal | 76–91 | 16 | A | | espaços |

### 3.3 Rodapé (tipo `9`)

| Campo | Posições | Comp. | Tipo | Especificação | Exemplo |
| --- | --- | --- | --- | --- | --- |
| Tipo de registo | 1–1 | 1 | A | `9` | `9` |
| Número total de registos | 2–8 | 7 | N | | `0000014` |
| Valor total dos pagamentos transferidos | 9–24 | 16 | N | 2 casas decimais | `0000000008074000` (80 740,00) |
| Valor total das comissões | 25–40 | 16 | N | 2 casas decimais | `0000000000007500` (75,00 = soma das comissões dos detalhes) |

O exemplo tem 14 detalhes; 13 com comissão 5,00 e um (`02295721110`, de 22/11/2012 14:30:44) com 10,00.

## 4. Diferenças entre os exemplos e a especificação

Os leitores do S5-08 **toleram** todas estas diferenças; nenhuma rejeita o ficheiro.

| Formato | Campo | Especificação | Exemplo | Regra do leitor |
| --- | --- | --- | --- | --- |
| MEPS | A035 data/hora | 10 dígitos `AAAAMMDDHH` | 12 dígitos `AAAAMMDDHHMM` (`201211221034`), com o campo de 12 posições | lê 12 dígitos; se os 2 últimos forem espaços, lê `AAAAMMDDHH` e minutos `00` |
| MEPS | A003 / A004 | fixos `50000000` / `00000100` | `00000008` / `00090002` | aceita quaisquer 8 dígitos; não valida o valor |
| MEPS | A006 moeda | `508` | `943` (código ISO 4217 actual do metical, MZN) | aceita `508` e `943`; outro → `PARSE_ERROR` |
| MEPS | A031 taxa de IVA | 2 numéricos | literalmente `XX` | não lê o campo |
| MEPS | A032 tarifa | sem casas decimais indicadas | 2 casas (`000001000` = 10,00); o rodapé A027 `000000011000` = 110,00 para 11 registos | lê em centavos (2 casas) |
| MEPS | A023 IVA (rodapé) | 12 numéricos | `00000000000 ` (11 dígitos e um espaço: desalinhado 1 carácter) | não valida; lê só os dígitos, se houver, e ignora o resto |
| MEPS / BMEPS | identificadores de transacção (A008, A014, A016, A017; "ID da transacção") | numéricos | só zeros | zeros = sem identificador (`bankTransactionId = null`) |
| BMEPS | entidade | — | não vem no ficheiro | vem da configuração da seguradora (S5-01) |

Outras observações dos exemplos:

- O exemplo MEPS tem a mesma referência (`00620400869`, 7 440,00) paga duas vezes, às 09:26 e às 09:29 do mesmo dia: é o caso "referência paga duas vezes" (excepção `ALREADY_PAID`, S5-10).
- O último registo de cada exemplo termina com `\r\n`; não há linhas vazias.
- Nenhum dos exemplos tem registos de estorno ou montantes negativos.

## 5. Pontos que a especificação não esclarece

Registados como decisões a validar nas fichas (S5-07, S5-08, S5-09). Resumo:

- **Fuso horário** das datas e horas (A035, "Data"/"Hora", data de criação): lidas como hora local de `Africa/Maputo`.
- **Codificação:** "ASCII". O leitor lê em ISO-8859-1 (aceita bytes acima de 127 nos campos alfanuméricos, como a localidade do terminal) e exige dígitos nos campos numéricos.
- **Terminador de linha:** `\r\n` nos exemplos; o leitor aceita também `\n`. Linhas mais curtas do que o comprimento do registo são completadas com espaços à direita (há sistemas que cortam os espaços finais); mais compridas → `PARSE_ERROR`.
- **Identificador único do pagamento:** os campos existem mas vêm a zeros; a idempotência de cada registo faz-se pelo ficheiro e pela linha (S5-06), e a de cada ficheiro pelo conteúdo (SHA-256) e pelo identificador do ficheiro no formato (MEPS A005; BMEPS empresa + perfil + ID do ficheiro) (S5-09).
- **Montante creditado:** os ficheiros trazem o montante pago e, à parte, a tarifa (MEPS) ou a comissão (BMEPS). A MoSeguros reconcilia pelo montante pago (o da referência) e guarda a tarifa/comissão só para informação.
