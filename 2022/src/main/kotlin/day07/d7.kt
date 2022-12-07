package day07

import util.getResourceAsFile

fun main() {
    getResourceAsFile("day07.txt").useLines { lines ->
        val iter = lines.iterator()

        val rootCd = FileSystemCommand.from(iter.next()) as FileSystemCommand.ChangeDirectory
        check(rootCd.isIntoRoot())

        val root = process(FileSystemEntry.ROOT, FileSystemEntry.ROOT, iter.next(), iter)

        // part 1
        val under100k = root.findDirsRecursive { it.totalSize() < 100_000 }
        println(under100k.sumOf { it.totalSize() })

        // part 2
        val totalSpace = 70_000_000
        val unusedSpace = totalSpace - root.totalSize()
        val minDelete = root.findDirsRecursive { unusedSpace + it.totalSize() >= 30_000_000 }.minBy { it.totalSize() }
        println(minDelete.totalSize())
    }
}

fun process(
    root: FileSystemEntry.Dir,
    workingDir: FileSystemEntry.Dir,
    line: String,
    rest: Iterator<String>
): FileSystemEntry.Dir =
    if (line.startsWith(FileSystemCommand.PREFIX)) {
        when (val cmd = FileSystemCommand.from(line)) {
            is FileSystemCommand.ChangeDirectory -> when {
                cmd.isIntoRoot() ->
                    if (rest.hasNext())
                        process(root, root, rest.next(), rest)
                    else
                        root
                cmd.isIntoParent() ->
                    if (rest.hasNext())
                        process(root, workingDir.parent!!, rest.next(), rest)
                    else
                        root
                else -> // isIntoChild
                    if (rest.hasNext())
                        process(root, workingDir.childDir(cmd.arg), rest.next(), rest)
                    else
                        root
            }
            is FileSystemCommand.List ->
                if (rest.hasNext())
                    process(root, workingDir, rest.next(), rest)
                else
                    root
        }
    } else {
        FileSystemEntry.fromListOutput(line, workingDir)
        if (rest.hasNext())
            process(root, workingDir, rest.next(), rest)
        else
            root
    }

sealed interface FileSystemEntry {
    val name: String
    val parent: Dir?

    fun totalSize(): Int

    fun findDirsRecursive(predicate: (FileSystemEntry) -> Boolean): List<FileSystemEntry>

    data class Dir(override val name: String, override val parent: Dir?) : FileSystemEntry {
        val entries = mutableSetOf<FileSystemEntry>()

        init {
            parent?.entries?.add(this)
        }

        fun childDir(name: String): Dir = entries.single { it.name == name } as Dir

        override fun totalSize(): Int = entries.sumOf { it.totalSize() }

        override fun findDirsRecursive(predicate: (FileSystemEntry) -> Boolean): List<FileSystemEntry> =
            if (predicate(this))
                entries.flatMap { it.findDirsRecursive(predicate) } + this
            else
                entries.flatMap { it.findDirsRecursive(predicate) }
    }

    data class File(override val name: String, val size: Int, override val parent: Dir) : FileSystemEntry {
        init {
            parent.entries.add(this)
        }

        override fun totalSize(): Int = size

        override fun findDirsRecursive(predicate: (FileSystemEntry) -> Boolean): List<FileSystemEntry> = emptyList()
    }

    companion object {
        val ROOT = Dir("/", null)

        fun fromListOutput(line: String, parent: Dir): FileSystemEntry {
            val parts = line.split(" ")
            return if (parts[0] == "dir")
                Dir(parts[1], parent)
            else
                File(parts[1], parts[0].toInt(), parent)
        }
    }
}

sealed interface FileSystemCommand {
    data class ChangeDirectory(val arg: String) : FileSystemCommand {
        fun isIntoRoot() = arg == FileSystemEntry.ROOT.name
        fun isIntoParent() = arg == ".."
    }

    object List : FileSystemCommand

    companion object {
        const val PREFIX = "$ "

        fun from(line: String): FileSystemCommand {
            val parts = line.split(" ")
            return when (parts[1]) {
                "cd" -> ChangeDirectory(parts[2])
                "ls" -> List
                else -> throw IllegalArgumentException("invalid cmd '$line'")
            }
        }
    }
}