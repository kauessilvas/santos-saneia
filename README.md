# Santos Saneia

Sistema Java Web para registrar e acompanhar chamados de problemas de esgoto em Santos. Desenvolvido com Spring Boot 4, Thymeleaf, Spring Security, Spring Data JPA e H2. A estrutura de pacotes e telas do [repositório-base](https://github.com/LeonardoSacra04/Projeto-Des-Servidores) foi mantida; a classe de inicialização e a configuração do Maven Wrapper foram acrescentadas para permitir a execução.

## Requisitos e execução

- Java 21
- Acesso à internet na primeira execução do Maven Wrapper

```bash
./mvnw spring-boot:run
```

Abra `http://localhost:8080`. O banco H2 é persistido na pasta `data/`, ignorada pelo Git. Para verificar a compilação e os testes, use `./mvnw test`.

## Perfis e fluxos

| Perfil | Acesso | Funções |
| --- | --- | --- |
| Cidadão | Público | Registra endereço, bairro, tipo, data, hora e imagem opcional; acompanha o chamado pelo número e endereço da página. |
| Sabesp | `/login` | Visualiza chamados, muda o status e publica comentários. |
| Prefeitura | `/login` | Consulta painel e relatório; filtra por status, tipo, bairro e período da ocorrência. |

Credenciais de demonstração: `sabesp` / `sabesp123` e `prefeitura` / `prefeitura123`. Antes de disponibilizar o sistema, configure senhas próprias pelas variáveis de ambiente `APP_SABESP_PASSWORD` e `APP_PREFEITURA_PASSWORD`.

Imagens aceitas: JPEG, PNG e WebP, até 5 MB. O número do chamado e o link de acompanhamento são mostrados após o cadastro. Os dados enviados são gravados no H2 local.

A data da ocorrência usa o seletor nativo do navegador. O cadastro aceita datas dos últimos cinco anos até a data atual; o mesmo limite é validado no servidor.

## Rotas principais

- `GET /cidadao/cadastro` e `POST /cidadao/chamados`: formulário e registro.
- `GET /cidadao/consulta/{id}`: acompanhamento público.
- `GET /sabesp` e `GET/POST /sabesp/{id}`: atendimento.
- `GET /prefeitura`, `/prefeitura/chamados` e `/prefeitura/relatorios`: acompanhamento institucional.
