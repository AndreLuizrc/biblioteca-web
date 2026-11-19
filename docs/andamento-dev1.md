# Andamento — DEV 1

Atualizado em 19/11/2026. Branch de trabalho: `feature/autores`.

Publicado em [AndreLuizrc/biblioteca-web](https://github.com/AndreLuizrc/biblioteca-web),
com `main` e `feature/autores`. O [PR #1](https://github.com/AndreLuizrc/biblioteca-web/pull/1)
está aberto, aguardando revisão e merge do DEV 2.

## Implementado

- Repositório Git local, base inicial em `main`, `.gitignore` e commits por etapa.
- Backend Java 17/Spring Boot 3.2: Autor, AutorRepository e os cinco endpoints de AutorController.
- Campos, validações, ordenação por nome, status 201/200/400/404 e preservação do ID e da criação no PUT.
- PostgreSQL 15 configurado para `biblioteca_db`; H2 separado no perfil de testes.
- Preenchimento automático de `criadoEm` com precisão de microssegundos.
- `LocalDateType`: JDBC 4.2 direto para manter datas históricas em colunas DATE, sem desvio de dia no H2.
- React 18: serviços Axios, lista e formulário de Autores, navegação e rotas de novo/editar.
- Validação dos formulários, campos opcionais, confirmação de exclusão, estados de carregamento e erro.
- Maven Wrapper 3.9.9; ajuste do script Windows para cache em caminhos com espaços e checksum da distribuição.
- Node 20 indicado em `.nvmrc`; lockfile reproduzível com peer TypeScript 4.9.5 compatível com react-scripts 5.
- Configuração compartilhada preparada na branch: Dockerfiles, Nginx, Compose com três serviços e workflow CI.
- README com requisitos, execução, testes e instruções para publicar e revisar.

## Validações realizadas

| Verificação | Resultado |
| --- | --- |
| Java usado | Eclipse Temurin 17.0.21 |
| Maven usado | Wrapper 3.9.9, validado em caminho com espaços |
| `mvnw.cmd package` | BUILD SUCCESS; 13 testes backend, nenhuma falha ou erro; JAR gerado |
| Controllers | 12 testes: CRUD, criação 201, 404 em GET/PUT/DELETE, validação, ordenação e preservação de dados |
| Persistência | 1 teste com SpringBootTest/MockMvc/H2, requisições com transações separadas e limpeza dos dados |
| Datas históricas | `1839-06-21` preservada em POST, armazenamento e GET no fuso local do ambiente |
| Node/npm usados no fechamento | Node 20.19.0 e npm 10.8.2 |
| `npm ci` | Instalação limpa concluída com o lockfile versionado |
| Jest | 4 testes passaram após a instalação limpa |
| `npm run build` | Compiled successfully |
| Navegador | Edge headless, frontend de produção e API real em H2 temporário; CRUD, ordenação, opcionais e 404 passaram |
| Layout | Lista e formulário conferidos; viewport desktop 1366×900 e lista mobile 390×844 |
| Compose | `docker compose config --quiet` passou |

Os dados usados no navegador foram criados apenas para a verificação e removidos ao final.
As ferramentas baixadas e os caches ficam em `.tools/` e `.cache/`, ignorados pelo Git.

### Decisão sobre datas

O teste no navegador revelou desvio de um dia ao persistir uma data anterior a 1900
no H2 com a conversão legada para `java.sql.Date`. A implementação usa `setObject`
e `getObject` com `LocalDate` em um `UserType`, mantendo Java `LocalDate` e SQL `DATE`.
O teste de persistência cobre o caso com o mesmo exemplo de nascimento do enunciado.
Isso evita depender do timezone configurado no computador do laboratório.

Referências técnicas: [H2 — DATE](https://h2database.com/html/datatypes.html#date_type)
e [Hibernate 6.4 — UserType](https://docs.hibernate.org/orm/6.4/javadocs/org/hibernate/usertype/UserType.html).

## Evidências disponíveis

- [Lista de Autores](evidencias/03-autores-lista.png).
- [Formulário de Autores](evidencias/04-autores-formulario.png).
- [Trechos dos resultados reais de validação](evidencias/validacao-dev1.txt).

O arquivo de texto registra os resultados; não substitui o print do terminal com
`BUILD SUCCESS` solicitado para a entrega final. Os prints de Livros, do vínculo pela API
e do Actions dependem das etapas seguintes.

## Pendências para a entrega completa

- DEV 2 precisa revisar, comentar e fazer o merge do PR #1 de Autores para `main`.
- DEV 2 cria/atualiza `feature/livros` a partir da base integrada e implementa sua funcionalidade.
- DEV 2 cria `IntegracaoTest.deveVincularLivroAAutor`, sem substituir o teste por mocks.
- Revisar e integrar o PR de Livros com comentário do DEV 1.
- Executar o workflow no GitHub e capturar Actions verde da versão final.
- Executar containers caso desejado para aceitação extra; o Docker engine estava desligado na validação local.
- Preencher os nomes da dupla e turma no README e reunir os prints restantes.

Não houve merge local para `main`, simulação de revisão do colega ou implementação de Livros nesta branch.
