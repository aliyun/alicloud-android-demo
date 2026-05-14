package com.aliyun.apm.android.demo.ui

internal data class ConfirmDialogConfig(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit
)

internal data class AcknowledgeDialogConfig(
    val title: String,
    val message: String
)

internal data class AdvancedAcknowledgeDialogConfig(
    val title: String,
    val sections: List<AdvancedAcknowledgeSection>
)

internal data class AdvancedAcknowledgeSection(
    val title: String,
    val description: String
)

internal fun javaCrashConfirmDialogConfig(
    title: String,
    onConfirm: () -> Unit
): ConfirmDialogConfig {
    return ConfirmDialogConfig(
        title = title,
        message = "即将触发【$title】类型崩溃，App将闪退，稍后即可在 EMAS控制台 看到崩溃日志。",
        onConfirm = onConfirm
    )
}

internal fun nativeCrashConfirmDialogConfig(
    title: String,
    onConfirm: () -> Unit
): ConfirmDialogConfig {
    return ConfirmDialogConfig(
        title = title,
        message = "即将触发【$title】类型崩溃，App将闪退。崩溃日志会在下次启动App后上报，随后即可在 EMAS控制台 查看。",
        onConfirm = onConfirm
    )
}

internal fun customConfirmDialogConfig(
    title: String,
    message: String,
    onConfirm: () -> Unit
): ConfirmDialogConfig {
    return ConfirmDialogConfig(
        title = title,
        message = message,
        onConfirm = onConfirm
    )
}

sealed interface MemoryIssueTriggerResult {
    object Triggered : MemoryIssueTriggerResult
    object AlreadyTriggered : MemoryIssueTriggerResult
}
