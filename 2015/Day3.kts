import java.io.File

val position = (0, 0)

val input = File("day3.txt")

when (instruction) {
    '^' -> position = (position.x, position.y + 1)
    '>' -> position = (position.x + 1, position.y)
    'v' -> position = (position.x, position.y - 1)
    '<' -> position = (position.x - 1, position.y)
}

println(abs(position.x) + abs(position.y))
