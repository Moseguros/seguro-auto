# Ficheiros de teste dos leitores MEPS e BMEPS (S5-08 §5)

- `exemplo-ficheiro-meps.txt` e `exemplo-ficheiro-bmeps.txt`: cópias **exactas** (byte a byte, `\r\n`) de `docs/specs/interbancos/`. O `OfficialExamplesAreCopiesTest` confirma-o (e também as cópias do simulador em `infra/mocks/ponto24-mock/src/main/resources/examples/`).
- Os restantes `.txt` derivam dos exemplos e cada `expected-<ficheiro>.csv` tem o resultado esperado, lido pelos `MepsFileParserTest` e `BmepsFileParserTest`:
  - uma linha por registo: `lineNo,entity,reference,amountCentavos,feeCentavos,paidAt,channel,bankTransactionId` (`paidAt` em ISO UTC; campos `null` vazios);
  - ou uma só linha `error,<código>,<lineNo>` (`lineNo` vazio quando o erro é do ficheiro inteiro, como os do rodapé).
- Fontes dos testes: MEPS com a entidade `90002`; BMEPS com a entidade `90090`, empresa `800` e perfil `02` (os dos exemplos e dos dados de demonstração, S5-01).
- Todos os `.txt` desta pasta estão com `-text` no `.gitattributes` (ficam com `\r\n`, menos o `meps-ok-lf.txt`).

Para gerar outra vez (nesta pasta; o resultado tem de ser igual ao que está no Git):

```bash
sed -n '/^```python$/,/^```$/p' README.md | sed '1d;$d' > /tmp/gen.py && python /tmp/gen.py
```

```python
# Gera os ficheiros derivados e os expected-*.csv a partir dos exemplos oficiais (só a biblioteca padrão).
from datetime import datetime, timedelta

MEPS = open("exemplo-ficheiro-meps.txt", "rb").read().decode("latin-1")
BMEPS = open("exemplo-ficheiro-bmeps.txt", "rb").read().decode("latin-1")
MAPUTO = timedelta(hours=2)  # Africa/Maputo: UTC+2, sem hora de Verão


def lines(text):
    return text.split("\r\n")[:-1]  # o último registo também termina em \r\n


def join(ls, eol="\r\n"):
    return "".join(l + eol for l in ls)


def put(line, pos, value):  # pos conta de 1, como na especificação
    return line[:pos - 1] + value + line[pos - 1 + len(value):]


def utc(local):
    return (local - MAPUTO).strftime("%Y-%m-%dT%H:%M:%SZ")


def write(name, content, expected):
    open(name, "wb").write(content.encode("latin-1"))
    open("expected-" + name.replace(".txt", ".csv"), "w", newline="\n").write("".join(r + "\n" for r in expected))


def meps_rows(ls):
    entity = ls[0][39:44]
    rows = []
    for no, l in enumerate(ls[1:-1], start=2):
        a035 = l[15:27]
        local = datetime.strptime(a035[:10], "%Y%m%d%H") if a035[10:] == "  " else datetime.strptime(a035, "%Y%m%d%H%M")
        log = l[3:15]
        bank = "" if set(log) == {"0"} else log[:4] + "-" + log[4:]
        rows.append("%d,%s,%s,%d,%d,%s,%s,%s" % (no, entity, l[81:92], int(l[27:40]), int(l[40:49]), utc(local),
                                                l[49:51].strip(), bank))
    return rows


def bmeps_rows(ls, entity="90090"):
    rows = []
    for no, l in enumerate(ls[1:-1], start=2):
        local = datetime.strptime(l[44:52] + l[52:58], "%d%m%Y%H%M%S")
        tx = l[58:65]
        rows.append("%d,%s,%s,%d,%d,%s,,%s" % (no, entity, l[1:12], int(l[12:28]), int(l[28:44]), utc(local),
                                              "" if set(tx) == {"0"} else tx))
    return rows


def error(code, line_no=None):
    return ["error,%s,%s" % (code, "" if line_no is None else line_no)]


m = lines(MEPS)
b = lines(BMEPS)

# --- MEPS ---
write("exemplo-ficheiro-meps.txt", MEPS, meps_rows(m))
write("meps-ok-lf.txt", join(m, "\n"), meps_rows(m))
write("meps-ok-trimmed.txt", join([l.rstrip(" ") for l in m]), meps_rows(m))
a035_10 = [m[0]] + [put(l, 26, "  ") for l in m[1:-1]] + [m[-1]]
write("meps-ok-a035-10.txt", join(a035_10), meps_rows(a035_10))
write("meps-ok-currency-508.txt", join([put(m[0], 45, "508")] + m[1:]), meps_rows(m))
empty_trailer = "9" + "0" * 8 + "0" * 17 + "0" * 12 + "0" * 11 + " " + " " * 57
write("meps-ok-empty.txt", join([m[0], empty_trailer]), [])
write("meps-err-trailer-count.txt", join(m[:-1] + [put(m[-1], 2, "%08d" % 12)]), error("TRAILER_COUNT_MISMATCH"))
write("meps-err-trailer-total.txt", join(m[:-1] + [put(m[-1], 10, "%017d" % (int(m[-1][9:26]) + 1))]),
      error("TRAILER_TOTAL_MISMATCH"))
write("meps-err-entity.txt", join([put(m[0], 40, "90003")] + m[1:]), error("ENTITY_MISMATCH", 1))
write("meps-err-processing-code.txt", join(m[:3] + [put(m[3], 2, "05")] + m[4:]), error("UNSUPPORTED_RECORD", 4))
write("meps-err-currency.txt", join([put(m[0], 45, "840")] + m[1:]), error("PARSE_ERROR", 1))
write("meps-err-line-too-long.txt", join(m[:2] + [m[2] + " "] + m[3:]), error("PARSE_ERROR", 3))
write("meps-err-no-trailer.txt", join(m[:-1]), error("MISSING_TRAILER", 12))

# --- BMEPS ---
write("exemplo-ficheiro-bmeps.txt", BMEPS, bmeps_rows(b))
write("bmeps-ok-empty.txt", join([b[0], "9" + "0" * 7 + "0" * 16 + "0" * 16]), [])
write("bmeps-err-company.txt", join([put(b[0], 2, "801")] + b[1:]), error("ENTITY_MISMATCH", 1))
write("bmeps-err-trailer-total.txt", join(b[:-1] + [put(b[-1], 9, "%016d" % (int(b[-1][8:24]) - 1))]),
      error("TRAILER_TOTAL_MISMATCH"))
write("bmeps-err-date.txt", join(b[:4] + [put(b[4], 45, "31022012")] + b[5:]), error("PARSE_ERROR", 5))
write("bmeps-err-control-char.txt", join(b[:2] + [put(b[2], 80, "\x00")] + b[3:]), error("ENCODING_ERROR", 3))
write("bmeps-warn-fees.txt", join(b[:-1] + [put(b[-1], 25, "%016d" % (int(b[-1][24:40]) + 100))]), bmeps_rows(b))
```
