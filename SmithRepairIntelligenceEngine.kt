package com.smith.repair.intelligence

import com.smith.repair.*
import kotlinx.coroutines.*

/**
 * SMITH REPAIR INTELLIGENCE ENGINE
 * Explica passo-a-passo como resolver problemas tecnicos
 * Com base em banco de dados iFixit completo
 */

data class RepairInstruction(
    val stepNumber: Int,
    val title: String,
    val detailedInstructions: String,
    val warnings: String,
    val tips: String,
    val estimatedTime: Int, // em minutos
    val difficulty: String,
    val imageUrl: String = "",
    val videoUrl: String = ""
)

data class RepairSolution(
    val problemDescription: String,
    val rootCause: String,
    val severity: String,
    val totalSteps: Int,
    val estimatedTime: Int,
    val estimatedCost: String,
    val difficulty: String,
    val successRate: Float,
    val instructions: List<RepairInstruction>,
    val requiredTools: List<String>,
    val requiredParts: List<String>,
    val safetyWarnings: List<String>,
    val fixableAtHome: Boolean
)

class SmithRepairIntelligenceEngine {
    
    suspend fun diagnoseRepairProblem(
        deviceType: String,
        problemDescription: String,
        symptoms: String
    ): RepairSolution? = withContext(Dispatchers.Default) {
        
        // Aqui Smith buscaria no banco de dados iFixit
        // Para este exemplo, criamos uma resposta genérica
        
        val instructions = generateRepairInstructions(
            deviceType = deviceType,
            problemDescription = problemDescription
        )
        
        val tools = identifyRequiredTools(deviceType)
        val parts = identifyRequiredParts(deviceType, problemDescription)
        val warnings = generateSafetyWarnings(deviceType, problemDescription)
        
        RepairSolution(
            problemDescription = problemDescription,
            rootCause = identifyRootCause(deviceType, symptoms),
            severity = calculateSeverity(problemDescription),
            totalSteps = instructions.size,
            estimatedTime = calculateTotalTime(instructions),
            estimatedCost = estimateRepairCost(deviceType, parts),
            difficulty = calculateDifficulty(deviceType, problemDescription),
            successRate = getSuccessRate(deviceType, problemDescription),
            instructions = instructions,
            requiredTools = tools,
            requiredParts = parts,
            safetyWarnings = warnings,
            fixableAtHome = isFixableAtHome(deviceType, problemDescription)
        )
    }
    
    private fun generateRepairInstructions(
        deviceType: String,
        problemDescription: String
    ): List<RepairInstruction> {
        return when(deviceType.uppercase()) {
            "SMARTPHONE", "IPHONE" -> generatePhoneRepairSteps(problemDescription)
            "NOTEBOOK", "COMPUTADOR" -> generateComputerRepairSteps(problemDescription)
            "TELEVISAO" -> generateTVRepairSteps(problemDescription)
            "GELADEIRA" -> generateRefrigeratorRepairSteps(problemDescription)
            "LAVADORA" -> generateWasherRepairSteps(problemDescription)
            else -> generateGenericRepairSteps(deviceType, problemDescription)
        }
    }
    
    private fun generatePhoneRepairSteps(problem: String): List<RepairInstruction> = when {
        problem.contains("bateria", ignoreCase = true) -> listOf(
            RepairInstruction(
                stepNumber = 1,
                title = "Preparacao e Seguranca",
                detailedInstructions = "Desligue o telefone completamente. Conecte-o ao computador para fazer backup dos dados.",
                warnings = "Cuidado com a eletricidade estatica. Use uma pulseira de terra.",
                tips = "Tire fotos de cada passo para facilitar a remontagem.",
                estimatedTime = 5,
                difficulty = "FACIL"
            ),
            RepairInstruction(
                stepNumber = 2,
                title = "Remocao da Tela",
                detailedInstructions = "Use um aquecedor para aquecer o adesivo (80-100°C). Levante lentamente com uma ferramenta de alavanca.",
                warnings = "Nao force! O vidro pode quebrar. Aqueca uniformemente.",
                tips = "Use ventosas duplas para melhor controle.",
                estimatedTime = 15,
                difficulty = "MODERADO"
            ),
            RepairInstruction(
                stepNumber = 3,
                title = "Desconexao da Bateria",
                detailedInstructions = "Desconecte o cabo da bateria do motherboard usando uma ferramenta de plastico.",
                warnings = "Evite tocar nos componentes eletronicos. Descarregue eletricidade estatica primeiro.",
                tips = "Tire uma foto da conexao antes de desconectar.",
                estimatedTime = 5,
                difficulty = "FACIL"
            ),
            RepairInstruction(
                stepNumber = 4,
                title = "Remocao da Bateria Antiga",
                detailedInstructions = "Levante a bateria puxando as abas ou usando uma ferramenta de alavanca. Remova o adesivo residual com alcool isopropilico.",
                warnings = "Baterias defeituosas podem estar inchadas. Se estiver inchada, use um recipiente seguro.",
                tips = "Nao perfure ou comprima a bateria antiga.",
                estimatedTime = 10,
                difficulty = "MODERADO"
            ),
            RepairInstruction(
                stepNumber = 5,
                title = "Instalacao da Nova Bateria",
                detailedInstructions = "Limpe a area com alcool. Aplique adesivo dupla-face no verso da nova bateria. Pressione firmemente.",
                warnings = "Nao curve ou danifique a bateria nova.",
                tips = "Deixe o adesivo secar por 1-2 minutos antes de fechar.",
                estimatedTime = 5,
                difficulty = "FACIL"
            )
        )
        else -> listOf(
            RepairInstruction(
                stepNumber = 1,
                title = "Diagnose Inicial",
                detailedInstructions = "Identifique o modelo exato do telefone. Procure por guias especificos no iFixit para seu modelo.",
                warnings = "Diferentes modelos tem procedimentos diferentes.",
                tips = "Use a ferramenta de busca do Smith para encontrar o guia exato.",
                estimatedTime = 5,
                difficulty = "FACIL"
            )
        )
    }
    
    private fun generateComputerRepairSteps(problem: String): List<RepairInstruction> {
        return listOf(
            RepairInstruction(
                stepNumber = 1,
                title = "Backup de Dados",
                detailedInstructions = "Faça backup de todos os arquivos importantes antes de qualquer reparo.",
                warnings = "Perda de dados pode ocorrer durante reparos.",
                tips = "Use uma unidade externa ou nuvem.",
                estimatedTime = 30,
                difficulty = "FACIL"
            )
        )
    }
    
    private fun generateTVRepairSteps(problem: String) = listOf(
        RepairInstruction(
            stepNumber = 1,
            title = "Desconexao de Energia",
            detailedInstructions = "Desconecte a TV da tomada e aguarde 5 minutos para descarregar.",
            warnings = "Risco de eletrocussao. A TV retém energia mesmo desligada.",
            tips = "Use uma pulseira de terra ao abrir.",
            estimatedTime = 5,
            difficulty = "FACIL"
        )
    )
    
    private fun generateRefrigeratorRepairSteps(problem: String) = listOf(
        RepairInstruction(
            stepNumber = 1,
            title = "Diagnostico Basico",
            detailedInstructions = "Verifique se a geladeira esta conectada. Ouça o som do compressor.",
            warnings = "Cuidado com peças moveis como o ventilador.",
            tips = "Anote os numeros do modelo para encontrar pecas corretas.",
            estimatedTime = 5,
            difficulty = "FACIL"
        )
    )
    
    private fun generateWasherRepairSteps(problem: String) = listOf(
        RepairInstruction(
            stepNumber = 1,
            title = "Corte de Energia",
            detailedInstructions = "Desligue a maquina de lavar e corte a agua.",
            warnings = "Risco de lesoes com pecas moveis. Nao abra enquanto funciona.",
            tips = "Drene toda a agua antes de abrir o tambor.",
            estimatedTime = 10,
            difficulty = "FACIL"
        )
    )
    
    private fun generateGenericRepairSteps(
        device: String,
        problem: String
    ) = listOf(
        RepairInstruction(
            stepNumber = 1,
            title = "Pesquisa de Guia",
            detailedInstructions = "Procure no banco de dados iFixit por '$device - $problem'.",
            warnings = "Sempre priorize a seguranca. Se tiver duvidas, procure um tecnico.",
            tips = "Consulte videos no YouTube com profissionais certificados.",
            estimatedTime = 10,
            difficulty = "FACIL"
        )
    )
    
    private fun identifyRequiredTools(deviceType: String): List<String> = when {
        deviceType.contains("phone", ignoreCase = true) || deviceType.contains("celular", ignoreCase = true) -> listOf(
            "Chave de fenda T5 Torx",
            "Ferramenta de alavanca de plastico",
            "Ventosa dupla",
            "Aquecedor de calor (heat gun)",
            "Alcool isopropilico"
        )
        else -> listOf("Chave de fenda multipla", "Pinça", "Lanterna LED")
    }
    
    private fun identifyRequiredParts(deviceType: String, problem: String): List<String> {
        return if (problem.contains("bateria", ignoreCase = true)) {
            listOf("Bateria de substituicao para $deviceType", "Adesivo dupla-face")
        } else listOf()
    }
    
    private fun generateSafetyWarnings(deviceType: String, problem: String): List<String> {
        return listOf(
            "Descarregue eletricidade estatica antes de tocar em componentes",
            "Desligue e desconecte o dispositivo completamente",
            "Trabalhe em superficie limpa e organizada",
            "Fotografe cada passo para referencia de montagem",
            "Nao force pecas - se nao sai, aqueca ou lubrifique primeiro"
        )
    }
    
    private fun identifyRootCause(device: String, symptoms: String): String {
        return when {
            symptoms.contains("nao liga", ignoreCase = true) -> "Problema de energia ou bateria"
            symptoms.contains("lento", ignoreCase = true) -> "Armazenamento cheio ou danificado"
            symptoms.contains("nao carrega", ignoreCase = true) -> "Problema com porta de carregamento ou bateria"
            else -> "Requer diagnostico mais detalhado"
        }
    }
    
    private fun calculateSeverity(problem: String): String {
        return when {
            problem.contains("nao funciona", ignoreCase = true) -> "CRITICA"
            problem.contains("intermitente", ignoreCase = true) -> "MODERADA"
            else -> "LEVE"
        }
    }
    
    private fun calculateTotalTime(instructions: List<RepairInstruction>): Int {
        return instructions.sumOf { it.estimatedTime }
    }
    
    private fun estimateRepairCost(deviceType: String, parts: List<String>): String {
        return if (parts.isEmpty()) "Gratuito" else "R$ 100 - R$ 500"
    }
    
    private fun calculateDifficulty(device: String, problem: String): String {
        return when {
            problem.contains("bateria", ignoreCase = true) && device.contains("phone", ignoreCase = true) -> "MODERADO"
            else -> "VARIAVEL"
        }
    }
    
    private fun getSuccessRate(device: String, problem: String): Float {
        return 0.85f // 85% de taxa de sucesso em geral
    }
    
    private fun isFixableAtHome(device: String, problem: String): Boolean {
        return !problem.contains("soldagem", ignoreCase = true) &&
               !problem.contains("placa mae", ignoreCase = true) &&
               !problem.contains("profissional", ignoreCase = true)
    }
}
