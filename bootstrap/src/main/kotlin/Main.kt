import io.github.llmagentbuilder.launcher.jdkhttpsync.JdkHttpSyncLauncher

fun main() {
    JdkHttpSyncLauncher().launch(
        AgentBootstrap.boostrap(
            AgentConfig(
                profile = ProfileConfig("You are a helpful assistant"),
                memory = MemoryConfig(InMemoryMemoryConfig(true)),
            )
        )
    )
}