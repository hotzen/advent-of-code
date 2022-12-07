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
    nextLinesIter: Iterator<String>
): FileSystemEntry.Dir {
    val newWorkingDir = if (line.startsWith(FileSystemCommand.PREFIX)) {
        when (val cmd = FileSystemCommand.from(line)) {
            is FileSystemCommand.ChangeDirectory ->
                when {
                    cmd.isIntoRoot() ->
                        root
                    cmd.isIntoParent() ->
                        workingDir.parent!!
                    else -> // isIntoChild
                        workingDir.childDir(cmd.arg)
                }
            is FileSystemCommand.List ->
                workingDir // stay in working-dir
        }
    } else {
        FileSystemEntry.fromListOutput(line, workingDir)
        workingDir // stay in working-dir
    }

    return if (nextLinesIter.hasNext())
        process(root, newWorkingDir, nextLinesIter.next(), nextLinesIter)
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

        fun childDir(name: String): Dir = entries.single { it.name == name } as Dir

        override fun totalSize(): Int = entries.sumOf { it.totalSize() }

        override fun findDirsRecursive(predicate: (FileSystemEntry) -> Boolean): List<FileSystemEntry> =
            if (predicate(this))
                entries.flatMap { it.findDirsRecursive(predicate) } + this
            else
                entries.flatMap { it.findDirsRecursive(predicate) }
    }

    data class File(override val name: String, val size: Int, override val parent: Dir) : FileSystemEntry {
        override fun totalSize(): Int = size

        override fun findDirsRecursive(predicate: (FileSystemEntry) -> Boolean): List<FileSystemEntry> = emptyList()
    }

    companion object {
        val ROOT = Dir("/", null)

        fun fromListOutput(line: String, parent: Dir): FileSystemEntry {
            val parts = line.split(" ")
            val entry = if (parts[0] == "dir")
                Dir(parts[1], parent)
            else
                File(parts[1], parts[0].toInt(), parent)
            parent.entries.add(entry)
            return entry
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