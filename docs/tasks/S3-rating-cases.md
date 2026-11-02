# S3 — Casos de referência da tarifa interna

> **Provisórios — a validar com a seguradora piloto.** Tarifas, encargos e franquias abaixo são de demonstração (não são valores reais de nenhuma seguradora nem valores regulamentares). Quando a seguradora piloto entregar a tarifa e os casos reais, substituem-se este ficheiro e os ficheiros de teste, sem mudar código (questão do S3-01).

Fonte dos testes `ReferenceCasesTest` (S3-01) e `SimulatorReferenceCasesIT` (S3-02). Os ficheiros de `backend/src/test/resources/rating/reference/` são **cópias exactas** dos blocos JSON deste ficheiro:

| Ficheiro de teste | Bloco |
| --- | --- |
| `tariff-dp.json` | secção 2.1 |
| `tariff-rc.json` | secção 2.2 |
| `tariff-temp.json` | secção 2.3 |
| `charges.json` | secção 3 |
| `products.json` | secção 4 |
| `cases.json` | secção 6 |

A semântica (variáveis, ordem das linhas, arredondamentos) está no S3-01, secções 8 e 9. Os valores foram calculados à mão e confirmados com um script independente (Python, `decimal`, `ROUND_HALF_UP`).

## 1. Convenções

- Dinheiro em centavos (`450000` = 4 500,00 MT).
- Data de início de todos os casos: `2027-01-15`. As idades contam-se nessa data.
- Campos do veículo que não aparecem num caso (`make`, `model`, `engineCc`, `seats`, `grossWeightKg`, `plate`) são `null`; nenhuma tarifa destes casos os usa.
- `requestedCoverages` não inclui as coberturas obrigatórias (o `CoverageSelector` acrescenta-as).
- Encargos de todos os casos: os da secção 3.

## 2. Tarifas

### 2.1 `DEMO-AUTO` (`COMPREHENSIVE`) — `tariff-dp.json`

```json
{
  "schemaVersion": "rating-v1",
  "coverages": [
    {
      "coverageCode": "MTPL",
      "base": {
        "type": "AMOUNT",
        "rows": [
          { "label": "Ligeiro de passageiros, particular", "when": { "vehicleCategory": { "in": ["LIGHT_PASSENGER"] }, "vehicleUsage": { "in": ["PRIVATE"] } }, "value": 450000 },
          { "label": "Ligeiro de passageiros, táxi ou aluguer", "when": { "vehicleCategory": { "in": ["LIGHT_PASSENGER"] }, "vehicleUsage": { "in": ["TAXI", "RENTAL"] } }, "value": 900000 },
          { "label": "Ligeiro misto", "when": { "vehicleCategory": { "in": ["LIGHT_MIXED"] } }, "value": 600000 },
          { "label": "Motociclo", "when": { "vehicleCategory": { "in": ["MOTORCYCLE"] } }, "value": 250000 },
          { "label": "Pesado", "when": { "vehicleCategory": { "in": ["HEAVY"] } }, "value": 1500000 }
        ]
      },
      "factors": [
        { "code": "DRIVER_AGE", "label": "Idade do condutor", "rows": [
          { "when": { "driverAgeYears": { "lt": 25 } }, "value": "1.30" },
          { "when": { "driverAgeYears": { "gte": 25, "lt": 70 } }, "value": "1.00" },
          { "when": { "driverAgeYears": { "gte": 70 } }, "value": "1.15" } ] },
        { "code": "LICENCE_YEARS", "label": "Antiguidade da carta", "rows": [
          { "when": { "licenceYears": { "lt": 2 } }, "value": "1.25" },
          { "when": { "licenceYears": { "gte": 2 } }, "value": "1.00" } ] },
        { "code": "PROVINCE", "label": "Província de circulação", "rows": [
          { "when": { "province": { "in": ["MAPUTO_CITY", "MAPUTO"] } }, "value": "1.10" },
          { "when": {}, "value": "1.00" } ] }
      ]
    },
    {
      "coverageCode": "VTPL",
      "base": {
        "type": "RATE_OF_CAPITAL",
        "rows": [
          { "when": { "capital": { "lt": 600000000 } }, "value": "0.0004" },
          { "when": {}, "value": "0.0003" }
        ]
      }
    },
    {
      "coverageCode": "OD_COLLISION",
      "base": {
        "type": "RATE_OF_VEHICLE_VALUE",
        "rows": [
          { "label": "Até 4 anos", "when": { "vehicleAgeYears": { "lt": 5 } }, "value": "0.025" },
          { "label": "5 a 10 anos", "when": { "vehicleAgeYears": { "gte": 5, "lt": 11 } }, "value": "0.030" }
        ]
      },
      "factors": [
        { "code": "DRIVER_AGE", "label": "Idade do condutor", "rows": [
          { "when": { "driverAgeYears": { "lt": 25 } }, "value": "1.30" },
          { "when": {}, "value": "1.00" } ] },
        { "code": "TRACKER", "label": "Localizador", "rows": [
          { "when": { "hasTracker": true }, "value": "0.90" },
          { "when": {}, "value": "1.00" } ] },
        { "code": "PROVINCE", "label": "Província de circulação", "rows": [
          { "when": { "province": { "in": ["MAPUTO_CITY"] } }, "value": "1.15" },
          { "when": {}, "value": "1.00" } ] }
      ]
    },
    {
      "coverageCode": "OD_FIRE",
      "base": { "type": "RATE_OF_VEHICLE_VALUE", "rows": [ { "when": {}, "value": "0.0015" } ] }
    },
    {
      "coverageCode": "OD_NATURE",
      "base": { "type": "RATE_OF_VEHICLE_VALUE", "rows": [ { "when": {}, "value": "0.0020" } ] }
    },
    {
      "coverageCode": "OD_THEFT",
      "base": { "type": "RATE_OF_VEHICLE_VALUE", "rows": [ { "when": {}, "value": "0.0050" } ] },
      "factors": [
        { "code": "TRACKER", "label": "Localizador", "rows": [
          { "when": { "hasTracker": true }, "value": "0.70" },
          { "when": {}, "value": "1.00" } ] }
      ]
    }
  ]
}
```

### 2.2 `DEMO-RC` (`MTPL_ONLY`) — `tariff-rc.json`

É a `tariff-dp.json` só com as duas primeiras entradas de `coverages` (`MTPL` e `VTPL`), iguais carácter a carácter, e o mesmo `schemaVersion`; sem `durationFactors`:

```json
{ "schemaVersion": "rating-v1", "coverages": [ <entrada MTPL da 2.1>, <entrada VTPL da 2.1> ] }
```

### 2.3 `DEMO-TEMP` (`TEMPORARY`) — `tariff-temp.json`

```json
{
  "schemaVersion": "rating-v1",
  "durationFactors": [
    { "days": 7, "factor": "0.06" },
    { "days": 14, "factor": "0.10" },
    { "days": 30, "factor": "0.18" },
    { "days": 60, "factor": "0.32" },
    { "days": 365, "factor": "1" }
  ],
  "coverages": [
    {
      "coverageCode": "MTPL",
      "base": {
        "type": "AMOUNT",
        "rows": [
          { "when": { "vehicleCategory": { "in": ["LIGHT_PASSENGER"] }, "vehicleUsage": { "in": ["PRIVATE"] } }, "value": 450000 },
          { "when": { "vehicleCategory": { "in": ["LIGHT_MIXED"] } }, "value": 600000 },
          { "when": { "vehicleCategory": { "in": ["MOTORCYCLE"] } }, "value": 250000 }
        ]
      },
      "factors": [
        { "code": "DRIVER_AGE", "label": "Idade do condutor", "rows": [
          { "when": { "driverAgeYears": { "lt": 25 } }, "value": "1.30" },
          { "when": { "driverAgeYears": { "gte": 25, "lt": 70 } }, "value": "1.00" },
          { "when": { "driverAgeYears": { "gte": 70 } }, "value": "1.15" } ] }
      ]
    }
  ]
}
```

## 3. Encargos da seguradora — `charges.json`

```json
[
  { "code": "DEMO_STAMP", "labelPt": "Imposto de selo (provisório)", "labelEn": "Stamp duty (provisional)", "type": "PERCENT", "value": "2.5" },
  { "code": "DEMO_FEE", "labelPt": "Taxa fixa (provisória)", "labelEn": "Fixed fee (provisional)", "type": "FIXED", "value": 5000 }
]
```

## 4. Coberturas dos produtos — `products.json`

Capital da RC obrigatória = `MTPL_MIN_CAPITAL` (3 000 000 MT, valor inicial do S2-02). As franquias são de demonstração.

```json
[
  { "code": "DEMO-RC", "type": "MTPL_ONLY", "coverages": [
    { "code": "MTPL", "mandatory": true, "requiresCodes": [], "capitalType": "FIXED", "capitalOptions": [300000000], "deductibleType": "NONE", "deductibleValue": null },
    { "code": "VTPL", "mandatory": false, "requiresCodes": [], "capitalType": "OPTIONS", "capitalOptions": [500000000, 1000000000], "deductibleType": "NONE", "deductibleValue": null } ] },
  { "code": "DEMO-AUTO", "type": "COMPREHENSIVE", "coverages": [
    { "code": "MTPL", "mandatory": true, "requiresCodes": [], "capitalType": "FIXED", "capitalOptions": [300000000], "deductibleType": "NONE", "deductibleValue": null },
    { "code": "VTPL", "mandatory": false, "requiresCodes": [], "capitalType": "OPTIONS", "capitalOptions": [500000000, 1000000000], "deductibleType": "NONE", "deductibleValue": null },
    { "code": "OD_COLLISION", "mandatory": false, "requiresCodes": [], "capitalType": "VEHICLE_VALUE", "capitalOptions": [], "deductibleType": "PERCENT", "deductibleValue": "10" },
    { "code": "OD_FIRE", "mandatory": false, "requiresCodes": [], "capitalType": "VEHICLE_VALUE", "capitalOptions": [], "deductibleType": "NONE", "deductibleValue": null },
    { "code": "OD_NATURE", "mandatory": false, "requiresCodes": [], "capitalType": "VEHICLE_VALUE", "capitalOptions": [], "deductibleType": "PERCENT", "deductibleValue": "10" },
    { "code": "OD_THEFT", "mandatory": false, "requiresCodes": ["OD_COLLISION", "OD_FIRE", "OD_NATURE"], "capitalType": "VEHICLE_VALUE", "capitalOptions": [], "deductibleType": "PERCENT", "deductibleValue": "20" } ] },
  { "code": "DEMO-TEMP", "type": "TEMPORARY", "coverages": [
    { "code": "MTPL", "mandatory": true, "requiresCodes": [], "capitalType": "FIXED", "capitalOptions": [300000000], "deductibleType": "NONE", "deductibleValue": null } ] }
]
```

## 5. Resumo e cálculo à mão

`B` base, `f` factores pela ordem da tarifa, `fd` factor de duração, `P` prémio da cobertura (HALF_UP ao centavo), `PC` prémio comercial, `ST` subtotal, `A` ajuste, `T` total (para cima ao metical).

| Caso | Produto | Situação | Cálculo | `PC` | Selo 2,5 % | Taxa | `ST` | `A` | `T` |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| C01 | DEMO-RC | Ligeiro particular, Gaza, condutor 40 anos, carta 10 anos | MTPL 450000 × 1,00 × 1,00 × 1,00 = 450000 | 450000 | 11250 | 5000 | 466250 | 50 | **466300** |
| C02 | DEMO-RC | Ligeiro particular, Maputo Cidade, condutor 22 anos, carta 1 ano | MTPL 450000 × 1,30 × 1,25 × 1,10 = 804375 | 804375 | 20109 (20109,375) | 5000 | 829484 | 16 | **829500** |
| C03 | DEMO-RC | Táxi, Sofala, condutor faz 25 anos no dia de início, carta 5 anos; RC facultativa 5 000 000 MT | MTPL 900000 × 1,00 × 1,00 × 1,00 = 900000; VTPL 500000000 × 0,0004 = 200000 | 1100000 | 27500 | 5000 | 1132500 | 0 | **1132500** |
| C04 | DEMO-RC | Ligeiro misto (mercadorias), Maputo Província, condutor 72 anos, carta faz 2 anos no dia de início | MTPL 600000 × 1,15 × 1,00 × 1,10 = 759000 | 759000 | 18975 | 5000 | 782975 | 25 | **783000** |
| C05 | DEMO-AUTO | Ligeiro particular de 2024 (3 anos), 1 234 567,89 MT, Maputo Cidade, sem localizador, condutor 35 anos, carta 15 anos; danos próprios completos | MTPL 450000 × 1,10 = 495000; OD_COLLISION 123456789 × 0,025 = 3086419,725 × 1,00 × 1,00 × 1,15 = 3549382,68375 → 3549383; OD_FIRE × 0,0015 = 185185,1835 → 185185; OD_NATURE × 0,0020 = 246913,578 → 246914; OD_THEFT × 0,0050 = 617283,945 → 617284 | 5093766 | 127344 (127344,15) | 5000 | 5226110 | 90 | **5226200** |
| C06 | DEMO-AUTO | Ligeiro particular de 2019 (8 anos), 850 000 MT, Nampula, com localizador, condutor 23 anos, carta 3 anos; danos próprios completos | MTPL 450000 × 1,30 = 585000; OD_COLLISION 85000000 × 0,030 = 2550000 × 1,30 × 0,90 × 1,00 = 2983500; OD_FIRE 127500; OD_NATURE 170000; OD_THEFT 425000 × 0,70 = 297500 | 4163500 | 104088 (104087,5) | 5000 | 4272588 | 12 | **4272600** |
| C07 | DEMO-AUTO | Ligeiro particular de 2014 (13 anos), 600 000 MT; danos próprios | OD_COLLISION: nenhuma linha da base para 13 anos | — | — | — | — | — | `NOT_AVAILABLE` / `NO_MATCHING_ROW` (`OD_COLLISION`, `BASE`) |
| C08 | DEMO-RC | Autocarro de transporte de passageiros, Tete | MTPL: nenhuma linha da base para `BUS` | — | — | — | — | — | `NOT_AVAILABLE` / `NO_MATCHING_ROW` (`MTPL`, `BASE`) |
| C09 | DEMO-TEMP | Ligeiro particular, Inhambane, condutor 45 anos, 30 dias | MTPL 450000 × 1,00 × 0,18 = 81000 | 81000 | 2025 | 5000 | 88025 | 75 | **88100** |
| C10 | DEMO-TEMP | Motociclo, Zambézia, condutor 19 anos, 7 dias | MTPL 250000 × 1,30 × 0,06 = 19500 | 19500 | 488 (487,5) | 5000 | 24988 | 12 | **25000** |

O que cada caso prova:

- C01: caso simples e arredondamento do total para cima.
- C02: três factores agravantes e `HALF_UP` abaixo de `.5` no encargo.
- C03: limite `gte 25` inclusivo, `RATE_OF_CAPITAL` e ajuste zero.
- C04: limite `gte 2` inclusivo na carta e base sem condição de uso.
- C05: `HALF_UP` ao nível da cobertura (para cima e para baixo) e valor do veículo com centavos.
- C06: factor de desconto (localizador) e `HALF_UP` em `.5` exacto no encargo.
- C07 e C08: falta de linha aplicável na base, com o motivo e a tabela.
- C09 e C10: factor de duração e `HALF_UP` em `.5` exacto.

## 6. Casos — `cases.json`

```json
[
  { "id": "C01", "product": "DEMO-RC", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "GAZA", "year": 2018, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "1986-06-01", "licenceIssueDate": "2016-03-01"}, "requestedCoverages": [],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "450000.000000", "premiumAmount": 450000}], "commercialPremiumAmount": 450000, "charges": [{"code": "DEMO_STAMP", "amount": 11250}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 466250, "roundingAdjustmentAmount": 50, "totalAmount": 466300} },
  { "id": "C02", "product": "DEMO-RC", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "MAPUTO_CITY", "year": 2015, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "2004-03-10", "licenceIssueDate": "2025-06-01"}, "requestedCoverages": [],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "804375.000000", "premiumAmount": 804375}], "commercialPremiumAmount": 804375, "charges": [{"code": "DEMO_STAMP", "amount": 20109}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 829484, "roundingAdjustmentAmount": 16, "totalAmount": 829500} },
  { "id": "C03", "product": "DEMO-RC", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "TAXI", "provinceOfCirculation": "SOFALA", "year": 2020, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "2002-01-15", "licenceIssueDate": "2021-01-16"}, "requestedCoverages": [{"code": "VTPL", "capital": 500000000}],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "900000.000000", "premiumAmount": 900000}, {"code": "VTPL", "exactPremium": "200000.0000", "premiumAmount": 200000}], "commercialPremiumAmount": 1100000, "charges": [{"code": "DEMO_STAMP", "amount": 27500}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 1132500, "roundingAdjustmentAmount": 0, "totalAmount": 1132500} },
  { "id": "C04", "product": "DEMO-RC", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_MIXED", "usage": "GOODS", "provinceOfCirculation": "MAPUTO", "year": 2012, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "1954-11-02", "licenceIssueDate": "2025-01-15"}, "requestedCoverages": [],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "759000.000000", "premiumAmount": 759000}], "commercialPremiumAmount": 759000, "charges": [{"code": "DEMO_STAMP", "amount": 18975}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 782975, "roundingAdjustmentAmount": 25, "totalAmount": 783000} },
  { "id": "C05", "product": "DEMO-AUTO", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "MAPUTO_CITY", "year": 2024, "hasTracker": false, "commercialValue": 123456789}, "driver": {"birthDate": "1991-09-09", "licenceIssueDate": "2011-04-04"}, "requestedCoverages": [{"code": "OD_COLLISION"}, {"code": "OD_FIRE"}, {"code": "OD_NATURE"}, {"code": "OD_THEFT"}],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "495000.000000", "premiumAmount": 495000}, {"code": "OD_COLLISION", "exactPremium": "3549382.683750000", "premiumAmount": 3549383}, {"code": "OD_FIRE", "exactPremium": "185185.1835", "premiumAmount": 185185}, {"code": "OD_NATURE", "exactPremium": "246913.5780", "premiumAmount": 246914}, {"code": "OD_THEFT", "exactPremium": "617283.945000", "premiumAmount": 617284}], "commercialPremiumAmount": 5093766, "charges": [{"code": "DEMO_STAMP", "amount": 127344}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 5226110, "roundingAdjustmentAmount": 90, "totalAmount": 5226200} },
  { "id": "C06", "product": "DEMO-AUTO", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "NAMPULA", "year": 2019, "hasTracker": true, "commercialValue": 85000000}, "driver": {"birthDate": "2003-12-31", "licenceIssueDate": "2023-10-10"}, "requestedCoverages": [{"code": "OD_COLLISION"}, {"code": "OD_FIRE"}, {"code": "OD_NATURE"}, {"code": "OD_THEFT"}],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "585000.000000", "premiumAmount": 585000}, {"code": "OD_COLLISION", "exactPremium": "2983500.000000000", "premiumAmount": 2983500}, {"code": "OD_FIRE", "exactPremium": "127500.0000", "premiumAmount": 127500}, {"code": "OD_NATURE", "exactPremium": "170000.0000", "premiumAmount": 170000}, {"code": "OD_THEFT", "exactPremium": "297500.000000", "premiumAmount": 297500}], "commercialPremiumAmount": 4163500, "charges": [{"code": "DEMO_STAMP", "amount": 104088}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 4272588, "roundingAdjustmentAmount": 12, "totalAmount": 4272600} },
  { "id": "C07", "product": "DEMO-AUTO", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "GAZA", "year": 2014, "hasTracker": false, "commercialValue": 60000000}, "driver": {"birthDate": "1986-06-01", "licenceIssueDate": "2016-03-01"}, "requestedCoverages": [{"code": "OD_COLLISION"}, {"code": "OD_FIRE"}, {"code": "OD_NATURE"}, {"code": "OD_THEFT"}],
    "expected": {"status": "NOT_AVAILABLE", "reason": "NO_MATCHING_ROW", "detail": {"coverageCode": "OD_COLLISION", "table": "BASE"}} },
  { "id": "C08", "product": "DEMO-RC", "startDate": "2027-01-15", "durationDays": 365, "vehicle": {"category": "BUS", "usage": "PASSENGER_TRANSPORT", "provinceOfCirculation": "TETE", "year": 2017, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "1980-02-20", "licenceIssueDate": "2005-07-07"}, "requestedCoverages": [],
    "expected": {"status": "NOT_AVAILABLE", "reason": "NO_MATCHING_ROW", "detail": {"coverageCode": "MTPL", "table": "BASE"}} },
  { "id": "C09", "product": "DEMO-TEMP", "startDate": "2027-01-15", "durationDays": 30, "vehicle": {"category": "LIGHT_PASSENGER", "usage": "PRIVATE", "provinceOfCirculation": "INHAMBANE", "year": 2016, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "1981-05-05", "licenceIssueDate": "2001-09-01"}, "requestedCoverages": [],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "81000.0000", "premiumAmount": 81000}], "commercialPremiumAmount": 81000, "charges": [{"code": "DEMO_STAMP", "amount": 2025}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 88025, "roundingAdjustmentAmount": 75, "totalAmount": 88100} },
  { "id": "C10", "product": "DEMO-TEMP", "startDate": "2027-01-15", "durationDays": 7, "vehicle": {"category": "MOTORCYCLE", "usage": "PRIVATE", "provinceOfCirculation": "ZAMBEZIA", "year": 2022, "hasTracker": false, "commercialValue": null}, "driver": {"birthDate": "2007-08-08", "licenceIssueDate": "2025-09-01"}, "requestedCoverages": [],
    "expected": {"status": "OFFERED", "coverages": [{"code": "MTPL", "exactPremium": "19500.0000", "premiumAmount": 19500}], "commercialPremiumAmount": 19500, "charges": [{"code": "DEMO_STAMP", "amount": 488}, {"code": "DEMO_FEE", "amount": 5000}], "subtotalAmount": 24988, "roundingAdjustmentAmount": 12, "totalAmount": 25000} }
]
```
