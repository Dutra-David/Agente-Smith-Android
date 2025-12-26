# PROMPT PARA GEMINI (IA do Android Studio)

## Copie e Cole Este Prompt Inteiro no Gemini do Android Studio

---

### PROMPT COMPLETO PARA COLOCAR NO GEMINI:

```
Você é um especialista em desenvolvimento Android/Kotlin/Compose.

Meu projeto Agente-Smith-Android está travando na tela "Hello Android!" após a instalação.

PROBLEMA:
- App abre e mostra apenas "Hello Android!"
- Não sai dessa tela
- Não responde a comandos
- Trava indefinidamente

DIAGNÓSTICO JA REALIZADO:
1. MainActivity.kt - está correto (tem splash screen)
2. ChatViewModel.kt - init block não faz I/O bloqueante
3. ChatScreen.kt - recebe ViewModel como parâmetro (sem hiltViewModel())

MINHA SUSPEITA:
O problema pode ser em uma destas áreas (preciso que você investigue TODAS):

1. **MessageRepository ou Repository classes** - podem ter inicializações pesadas
2. **AppDatabase ou Room Database** - migration ou schema issues
3. **Hilt Module/Component** - dependências não se injetando corretamente
4. **AndroidManifest.xml** - faltando configurações ou activities
5. **build.gradle ou versioning** - conflitos de versão
6. **NavController ou Navigation** - estrutura de navegação quebrada
7. **Outras composables** - algo chamando a MainActivity que trava

TARASA PARA VOCÓ:

1. **ANALISE LINHA POR LINHA:
   - Todos os files em app/src/main/java/com/dutra/agente/
   - Procure por: inicializações em init {}, constructors com lógica pesada, database operations
   - Procure por: chamadas a APIs, network requests, file I/O

2. **VERIFIQUE ESPECIFICAMENTE:
   - app/src/main/java/com/dutra/agente/data/repository/** (TODOS os repositories)
   - app/src/main/java/com/dutra/agente/data/database/** (Database, DAOs)
   - app/src/main/java/com/dutra/agente/di/** (Hilt modules)
   - app/src/main/AndroidManifest.xml
   - app/build.gradle (versões, plugins)

3. **SE ENCONTRAR QUALQUER coisa com:**
   - val db = Room.databaseBuilder() em init ou constructor
   - Retrofit.Builder() em init ou constructor
   - viewModelScope.launch SEM try-catch
   - Qualquer I/O em thread principal
   - CORRIJA IMEDIATAMENTE

4. **CRIE SOLUCOES COMPLETAS:
   - Se for banco de dados: implemente Room.databaseBuilder() com LAZY initialization
   - Se for API: implemente inicialização assíncrona com runBlocking removido
   - Se for Hilt: verifique @Provides e @Binds são lazy
   - Se for NavController: verifique navController != null antes de usar

5. **FINAL:
   - Mostre TODAS as mudanças linha por linha
   - Explique EXATAMENTE por que cada mudança resolve o problema
   - Não deixe nada incompleto ou "pode arrumar depois"

VOU COPIAR TODO O CÓDIGO DO MEU PROJETO PARA VOCÓ:
(Cole aqui o conteúdo dos files)

POR FAVOR:
1. NÃO dé apenas sugestões - DE A SOLUÇÃO COMPLETA E PRONTA PARA COLAR
2. NÃO deixe nada "TODO" ou incompleto
3. EXPLIQUE cada linha que mudar
4. GARANTA que app NÃO vai travar depois das mudanças
```

---

## PASSOS PARA USAR ESTE PROMPT:

### Passo 1: Abrir Gemini no Android Studio
1. Tools > Google Gemini Code Assistance (ou pressione Ctrl+Shift+G no Windows/Linux, Cmd+Shift+G no Mac)
2. Ou procure por "Gemini" na barra de busca do Android Studio (Ctrl+Shift+A)

### Passo 2: Cole o Prompt
1. Abra o chat do Gemini
2. Copie o PROMPT COMPLETO acima (a parte dentro das aspas)
3. Cola no Gemini
4. Pressione Enter/Send

### Passo 3: Envia o CÓdigo
Depois que o Gemini responder, envie:

1. Abra Terminal no Android Studio (View > Tool Windows > Terminal)
2. Copie TODO o código das pastas chave:
   ```bash
   # Windows
   type app\src\main\java\com\dutra\agente\data\repository\*.kt
   type app\src\main\java\com\dutra\agente\data\database\*.kt
   type app\src\main\java\com\dutra\agente\di\*.kt
   type app\build.gradle
   type app\src\main\AndroidManifest.xml
   
   # Linux/Mac
   cat app/src/main/java/com/dutra/agente/data/repository/*.kt
   cat app/src/main/java/com/dutra/agente/data/database/*.kt
   cat app/src/main/java/com/dutra/agente/di/*.kt
   cat app/build.gradle
   cat app/src/main/AndroidManifest.xml
   ```

3. Cola o OUTPUT no Gemini com esta mensagem:
   "Aqui estã o código completo. Por favor, investigue todos esses files e mostre as correções necessárias."

### Passo 4: Aplica as Solucoes
O Gemini vai dar as soluções. Você:
1. Copia o código corrigido do Gemini
2. Cola nos files do seu projeto
3. Build > Rebuild Project
4. Run

---

## ALTERNATIVA: SE PREFERIR USAR APENAS ESTE PROMPT CURTO

Se o prompt acima for muito grande, use este MAIS CURTO:

```
App Android Kotlin/Compose travando na tela Hello Android.

Fi diagnosticou problemas em:
- MainActivity.kt
- ChatViewModel.kt  
- ChatScreen.kt

Mas app continua travando.

Preciso que você:
1. Revise TODOS files em app/src/main/java/com/dutra/agente/data/
2. Revise app/src/main/java/com/dutra/agente/di/
3. Procure por: inicializações pesadas, I/O em main thread, room database issues
4. Mostre solutions prontas para colar

Code:
[COLE AQUI TODO O CODIGO]
```

---

## DICA: Para copiar todo o código rápido

1. File > Show in Explorer (ou show in Finder no Mac)
2. Selecione a pasta app/src/main/java/com/dutra/agente/
3. Clique direito > Copy
4. Abra um editor de texto (Notepad++, VS Code)
5. Ctrl+V
6. Selecione tudo (Ctrl+A)
7. Ctrl+C
8. Cola no Gemini

---

## ESPERADO DO GEMINI

O Gemini deve responder com:
- «Arquivos com problemas encontrados»
- «Código corrigido linha por linha»
- «Explicação de cada correção»
- «Por que isso vai resolver o travamento»

Se receber respostas vagas, mande:
"Por favor, seja MUITO mais específicado. Mostre o código EXATO com linhas numeradas e explique cada mudança."

---

**BOA SORTE! O Gemini deve conseguir encontrar o problema! 🚀**
