package aoc

// def deepString(list: Seq[Any], sep: String = ","): String = {
//   list.map {
//     case nested: Seq[_] => deepString(nested, sep)
//     case nested: Array[_] => deepString(nested.toList, sep)
//     case elem => elem.toString
//   }.mkString(",")
// }