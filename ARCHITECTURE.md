# Arquitetura - Agente Smith Android

## Visão Geral

O Agente Smith Android segue a arquitetura **MVVM** com camadas bem definidas.

## Camadas

```
UI Layer (Jetpack Compose)
        │
Presentation Layer (ViewModels)
        │
Domain Layer (Usecases)
        │
Data Layer (Remote + Local)
        │
Backend Agent-S (API REST)
```

## Estrutura de Pacotes

- `ui/` - Telas e componentes Compose
- `viewmodel/` - Lógica de apresentação
- `domain/` - Lógica de negócio
- `data/` - Remote (Retrofit) + Local (Room)
- `di/` - Injeção de dependências (Hilt)

## Padrões

- **MVVM**: Model-View-ViewModel
- **Repository**: Abstração de dados
- **Hilt**: Injeção de dependências
- **Flow**: Programação reativa

## Endpoints Agent-S

| Método | Endpoint | Descrição |
|--------|----------|-------------|
| POST | `/api/chat` | Enviar mensagem |
| GET | `/api/status` | Status do servidor |
| POST | `/api/session` | Nova sessão |
| GET | `/api/session/{id}` | Histórico |

---
**Versão:** 1.0 | **Data:** Dec 2024
