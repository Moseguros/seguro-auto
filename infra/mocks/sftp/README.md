# SFTP local

Serviço `sftp` do `infra/docker-compose.yml` (S5-06): faz o papel do servidor SFTP da seguradora, onde o banco (InterBancos, rede Ponto24) deposita os ficheiros de movimentos MEPS ou BMEPS (`docs/specs/interbancos/`, ADR-007). Quem os lê é o `payment-file-worker` (S5-09).

Imagem `atmoz/sftp:alpine`, fixada pelo *digest* (a imagem não publica etiquetas de versão). Só existe para `amd64`: em máquinas `arm64` (Apple Silicon) corre por emulação, mais devagar.

## Utilizadores

| Utilizador | Palavra-passe | Seguradora | Formato | Pastas (relativas à raiz do utilizador) |
| --- | --- | --- | --- | --- |
| `demo` | `demo-sftp-pass` | Seguradora Demo (`DEMO`, entidade `90002`) | MEPS | `inbound`, `processed`, `error` |
| `demob` | `demob-sftp-pass` | Seguradora Demo B (`DEMOB`, entidade `90090`, empresa `800`, perfil `02`) | BMEPS | `inbound`, `processed`, `error` |

São os da configuração de demonstração do S5-01 (§8, `BANK_REFERENCE`): `sftp.host` `sftp`, porta `22` dentro da rede do compose, `inboundDir` `inbound`, `processedDir` `processed`, `errorDir` `error`. As palavras-passe são só de desenvolvimento; o backend grava-as no Vault no arranque (`DevPaymentSecretsSeeder`, S5-01) e a BD guarda só a referência.

Cada utilizador fica preso (*chroot*) na sua pasta; os ficheiros ficam nos volumes `sftpdata` (`demo`) e `sftpdatab` (`demob`), apagados com `docker compose down -v`.

## Ligar

Da máquina (porta `2222`):

```bash
sftp -P 2222 demo@localhost        # palavra-passe demo-sftp-pass
sftp> ls                           # error  inbound  processed
sftp> put docs/specs/interbancos/exemplo-ficheiro-meps.txt inbound/
```

```bash
sftp -P 2222 demob@localhost       # palavra-passe demob-sftp-pass
sftp> put docs/specs/interbancos/exemplo-ficheiro-bmeps.txt inbound/
```

Ou sem cliente SFTP, por dentro do contentor:

```bash
docker compose exec sftp ls -l /home/demo /home/demob
```

## Chave do servidor

A chave do servidor SSH é gerada quando o contentor é criado e **muda sempre que é recriado** (`docker compose up` depois de `down`, ou `--force-recreate`). Por isso:

- o `sftp` da máquina pode queixar-se de `REMOTE HOST IDENTIFICATION HAS CHANGED`: apagar a linha com `ssh-keygen -R "[localhost]:2222"`;
- o worker, no perfil `local`, aceita chaves desconhecidas (S5-09). Fora de `local` e `test` a fonte tem de ter `sftp.hostKeyFingerprint` (S5-01) e o worker recusa outra chave (`SFTP_HOST_KEY_MISMATCH`).
