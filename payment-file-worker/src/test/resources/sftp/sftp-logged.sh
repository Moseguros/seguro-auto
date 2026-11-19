#!/bin/sh
# Servidor SFTP dos testes do worker: o sftp-server do OpenSSH com o registo das operações num ficheiro
# (os testes contam as aberturas para leitura, "open ... flags READ").
exec /usr/lib/ssh/sftp-server -e -l INFO 2>>/tmp/sftp.log
