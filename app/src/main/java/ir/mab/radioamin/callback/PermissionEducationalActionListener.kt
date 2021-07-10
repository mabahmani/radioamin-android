package ir.mab.radioamin.callback

interface PermissionEducationalActionListener {
    fun onAllow(lmbd: (allowed: Boolean) -> Unit)
}