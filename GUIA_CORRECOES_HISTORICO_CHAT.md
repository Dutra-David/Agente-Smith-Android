# GUIA: Correcoes para HistoricoChat.kt

## Problemas Identificados

### 1. Pacote Incorreto: 'dados' vs 'data'
**Linha 1:**
```kotlin
package com.dutra.agente.dados.banco.entidades  // ERRADO
```

**DEVE SER:**
```kotlin
package com.dutra.agente.data.banco.entidades  // CORRETO
```

**Razao:** O arquivo esta em `app/src/main/java/com/dutra/agente/data/banco/entidades/`  
O package deve corresponder ao caminho do arquivo.

---

### 2. Referencia Faltando: UsuarioPerfil
**Erro:**
```
Unresolved reference 'UsuarioPerfil'
```

**Causa:** A classe `UsuarioPerfil` nao existe no pacote `com.dutra.agente.data.banco.entidades`

**Solucao:** Crie o arquivo `UsuarioPerfil.kt` na mesma pasta

---

### 3. LocalDateTime: TypeConverters Necessarios
**Erro esperado ao compilar:**
```
Cannot figure out how to save this field into database: LocalDateTime
```

**Causa:** Room Database nao sabe como persistir `LocalDateTime`

**Solucao:** Configurar TypeConverters no AppDatabase

---

## SOLUCAO COMPLETA (3 PASSOS)

### PASSO 1: Corrigir HistoricoChat.kt

Edite o arquivo: `app/src/main/java/com/dutra/agente/data/banco/entidades/HistoricoChat.kt`

Mude APENAS a primeira linha:
```kotlin
// MUDE ISTO:
package com.dutra.agente.dados.banco.entidades

// PARA ISTO:
package com.dutra.agente.data.banco.entidades
```

O resto do arquivo permanece igual.

---

### PASSO 2: Criar UsuarioPerfil.kt

Crie um novo arquivo: `app/src/main/java/com/dutra/agente/data/banco/entidades/UsuarioPerfil.kt`

Conteudo:
```kotlin
package com.dutra.agente.data.banco.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_perfil")
data class UsuarioPerfil(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val nome: String,
    
    val email: String? = null,
    
    val dataCriacao: String? = null,
    
    // Adicione outros campos conforme necessario
    val ativo: Boolean = true
)
```

---

### PASSO 3: Criar/Atualizar Converters.kt

Verifique se existe: `app/src/main/java/com/dutra/agente/data/banco/Converters.kt`

Se nao existir, CRIE com este conteudo:
```kotlin
package com.dutra.agente.data.banco

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
}
```

Depois, EDITE seu `AppDatabase.kt` e ADICIONE os converters:
```kotlin
@Database(
    entities = [HistoricoChat::class, UsuarioPerfil::class],
    version = 1
)
@TypeConverters(Converters::class)  // ADICIONE ESTA LINHA
abstract class AppDatabase : RoomDatabase() {
    abstract fun historicoDao(): HistoricoChatDao
    // ...
}
```

---

## PASSO 4: Testar

Execute no terminal:
```bash
./gradlew clean
./gradlew build
```

Se tudo estiver correto, nao devera ter erros de compilacao.

---

## CHECKLIST FINAL

- [ ] Corrigiu o package em HistoricoChat.kt (dados -> data)
- [ ] Criou UsuarioPerfil.kt
- [ ] Criou/Atualizou Converters.kt
- [ ] Adicionou @TypeConverters(Converters::class) no AppDatabase
- [ ] Executou ./gradlew clean
- [ ] Executou ./gradlew build com sucesso
- [ ] Build passou 100% sem erros

---

## RESUMO DOS ERROS RESOLVIDOS

| Erro | Causa | Solucao |
|------|-------|----------|
| Package mismatch | Pacote 'dados' vs 'data' | Mudar para 'data' |
| Unresolved UsuarioPerfil | Classe nao existe | Criar UsuarioPerfil.kt |
| Cannot save LocalDateTime | Sem conversor | Criar Converters.kt |

---

## ARQUIVOS NECESSARIOS

**A ATUALIZAR:**
- `app/src/main/java/com/dutra/agente/data/banco/entidades/HistoricoChat.kt` (apenas linha 1)
- `app/src/main/java/com/dutra/agente/data/banco/AppDatabase.kt` (adicionar @TypeConverters)

**A CRIAR:**
- `app/src/main/java/com/dutra/agente/data/banco/entidades/UsuarioPerfil.kt`
- `app/src/main/java/com/dutra/agente/data/banco/Converters.kt`
