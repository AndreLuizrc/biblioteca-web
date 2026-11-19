# Biblioteca Web

Aplicação do laboratório de Engenharia de Software para cadastrar Autores e Livros.
Nesta etapa, `feature/autores` implementa a parte do DEV 1: API e interface de Autores,
testes e configuração compartilhada. Livros e o teste de vínculo Autor–Livro são a próxima
etapa do DEV 2. A `main` preserva a base inicial até a revisão do colega.

## Tecnologias

| Camada | Tecnologias |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2.12, Maven, JPA, Validation, Lombok |
| Frontend | React 18.3.1, React Router 6, Axios, react-scripts 5, Node 20, npm |
| Banco | PostgreSQL 15; H2 em memória no perfil `test` |
| Testes | JUnit 5, Mockito, MockMvc, SpringBootTest e Jest |
| Infraestrutura | Docker Compose, Nginx e GitHub Actions |

## Divisão de trabalho

| Integrante | Branch | Responsabilidade |
| --- | --- | --- |
| DEV 1 — nome a preencher | `feature/autores` | Autor, repository, controller, testes, lista e formulário; estrutura e configurações compartilhadas |
| DEV 2 — nome a preencher | `feature/livros` | Livro, repository, controller, testes, lista, formulário com select; `IntegracaoTest` e validação final do CI |

Turma: a preencher. Cada integrante deve revisar, deixar pelo menos um comentário e
fazer o merge do PR do colega. Não apagar as branches antes da avaliação.

## Estrutura

```text
backend/
  pom.xml, mvnw, mvnw.cmd, Dockerfile
  src/main/java/com/biblioteca/{model,repository,controller}
  src/main/resources/application.properties
  src/test/java/com/biblioteca/
  src/test/resources/application-test.properties
frontend/
  package.json, package-lock.json, Dockerfile, nginx.conf
  public/index.html
  src/{components,services}, App.js, App.css
.github/workflows/ci-cd.yml
docker-compose.yml
docs/
```

Consulte a [spec](docs/spec-implementacao-biblioteca-web.md) para todos os requisitos e
o [acompanhamento do DEV 1](docs/andamento-dev1.md) para validações e pendências.

## Executar com Docker Compose

Requisito: Docker com o engine iniciado e Docker Compose disponível.

Na raiz:

```powershell
docker compose up -d --build
docker compose ps
```

- Interface: <http://localhost:3000/autores>.
- API: <http://localhost:8080/api/autores>.
- PostgreSQL: `localhost:5432`, banco `biblioteca_db`, usuário/senha de laboratório `postgres/postgres`.

O Nginx encaminha `/api` para o backend e permite abrir diretamente as rotas React.
O volume `postgres_data` persiste o banco entre reinicializações. `docker compose down`
encerra os serviços preservando esse volume. Se o ambiente usar o comando legado,
substitua `docker compose` por `docker-compose`.

## Executar localmente

Requisitos: JDK **17**, Node.js **20** e PostgreSQL **15**. A `.nvmrc` registra a linha
do Node. Configure `JAVA_HOME` para o JDK 17; o Java padrão do computador pode ser outro.
O Maven Wrapper baixa Maven na primeira execução, exigindo acesso à internet.

1. Na raiz, suba somente o banco: `docker compose up -d postgres`.
2. Em `backend/`, execute `./mvnw.cmd spring-boot:run` no Windows ou `./mvnw spring-boot:run` no Linux/macOS.
3. Em outro terminal, dentro de `frontend/`:

```powershell
npm ci
npm start
```

A API usa a porta 8080 e o React a 3000. Opcionalmente copie `frontend/.env.example`
para `frontend/.env.local` para configurar `REACT_APP_API_URL` e reinicie o frontend.
O backend aceita `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`; os valores padrão são os do
PostgreSQL local descrito acima. No Compose, o banco é acessado pelo host `postgres`.

## Testes e build

Em `backend/`, com Java 17:

```powershell
./mvnw.cmd test -Dtest="*ControllerTest"
./mvnw.cmd test
./mvnw.cmd package
```

Em Linux/macOS, use `./mvnw`. Se houver Maven instalado, `mvn` é equivalente.
Os testes de persistência usam H2 em memória pelo perfil `test`, sem PostgreSQL externo.
`AutorControllerTest` cobre criação, busca, atualização, exclusão, 404 e validação.
`AutorPersistenciaTest` verifica CRUD real, ordenação e preservação de `criadoEm`.
Este último complementa, mas não substitui, o `IntegracaoTest` de vínculo que o DEV 2 deve criar.

Em `frontend/`:

```powershell
npm ci
npm test -- --watchAll=false --passWithNoTests
npm run build
```

Os testes Jest verificam criação, edição com limpeza de opcionais, falha de carregamento
e exclusão. O workflow executa os testes backend e frontend e o build React em PRs
destinados à `main` e pushes na `main`. Sua execução no GitHub depende da publicação
do repositório; o YAML local sozinho não comprova Actions verde.

## API e telas de Autores

| Método | Endpoint | Resposta |
| --- | --- | --- |
| POST | `/api/autores` | 201 e autor criado |
| GET | `/api/autores` | 200, lista ordenada por nome |
| GET | `/api/autores/{id}` | 200 ou 404 |
| PUT | `/api/autores/{id}` | 200 ou 404 |
| DELETE | `/api/autores/{id}` | 200 ou 404 |

Corpo de POST/PUT:

```json
{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}
```

`nome` é obrigatório e tem até 100 caracteres; nacionalidade é opcional, com até 50;
data é opcional e segue `AAAA-MM-DD`. Valores inválidos retornam 400. ID e `criadoEm`
são definidos pelo backend; o PUT preserva esses dados. Campos opcionais aceitam `null`.
Não há coleção de livros em Autor, preservando o relacionamento unidirecional planejado.

Rotas disponíveis: `/autores`, `/autores/novo` e `/autores/editar/:id`.
As rotas e os endpoints de Livros serão adicionados pelo DEV 2. Quando houver livros
vinculados a um autor, removê-los antes de excluir o autor: a FK deverá proteger o vínculo.

## Publicação e revisão

Após criar um repositório GitHub vazio, configure o remoto e envie ambas as branches:

```powershell
git remote add origin URL_REAL_DO_REPOSITORIO
git push -u origin main
git push -u origin feature/autores
```

Abra PR de `feature/autores` para `main`, usando o [texto preparado](docs/pr-autores.md).
DEV 2 revisa e faz o merge. Depois atualiza sua `main` e cria `feature/livros` para
implementar sua parte; se já criou a branch na base inicial, deve incorporar a `main`.
Após Livros, DEV 1 revisa e integra o segundo PR. Registrar os links reais e coletar
os prints previstos na spec. O projeto completo ainda depende dessa etapa.
