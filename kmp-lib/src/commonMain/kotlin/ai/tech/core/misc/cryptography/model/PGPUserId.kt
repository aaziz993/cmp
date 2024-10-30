package ai.tech.core.misc.cryptography.model

import kotlinx.serialization.Serializable

@Serializable
public data class PGPUserId(
    val name: String? = null,
    val comment: String? = null,
    val email: String? = null,
) {

    public override fun toString(): String {
        val sb = StringBuilder()
        if (!name.isNullOrEmpty()) {
            sb.append(name)
        }
        if (!comment.isNullOrEmpty()) {
            sb.append(" (")
            sb.append(comment)
            sb.append(")")
        }
        if (!email.isNullOrEmpty()) {
            sb.append(" <")
            sb.append(email)
            sb.append(">")
        }
        return sb.toString()
    }

    public companion object {
        private val nr = """^[^<(].*?(?= [<(])""".trimMargin().toRegex()
        private val cr = """(?<=\().*(?=\))""".toRegex()
        private val er = """(?<=<).*(?=>)""".toRegex()

        public fun parse(userId: String): PGPUserId =
            PGPUserId(
                nr.find(userId)?.value,
                cr.find(userId)?.value,
                er.find(userId)?.value,
            )
    }

}