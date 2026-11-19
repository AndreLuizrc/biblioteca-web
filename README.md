# Biblioteca Web

Aplicação do laboratório de Engenharia de Software: CRUD de Autores e Livros.

## Tecnologias

| Camada | Tecnologias |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2.12, Maven, JPA, Validation, Lombok |
| Frontend | React 18, React Router 6, Axios, react-scripts 5, Node 20, npm |
| Banco | PostgreSQL 15; H2 em memória nos testes |
| Infraestrutura | Docker Compose e GitHub Actions |

## Divisão de trabalho

| Integrante | Branch | Responsabilidade |
| --- | --- | --- |
| DEV 1 — nome a preencher | `feature/autores` | CRUD de Autores, configuração inicial e arquivos comuns acordados |
| DEV 2 — nome a preencher | `feature/livros` | CRUD de Livros, vínculo com Autor, integração e finalização do CI |

Turma: a preencher. Cada integrante revisa e comenta o PR do colega antes do merge.

## Estrutura

`backend/`: aplicação Spring Boot. `frontend/`: aplicação React.
`docs/`: enunciados e [spec de implementação](docs/spec-implementacao-biblioteca-web.md).

O commit inicial contém somente a base. As funcionalidades serão desenvolvidas nas respectivas branches.
