package cnovaez.dev.todoappcompose.core

/**
 ** Created by Carlos A. Novaez Guerrero on 10/24/2023 11:54 AM
 ** cnovaez.dev@outlook.com
 **/
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Tasks : Routes("tasks")
//    object Screen3 : Routes("screen3")
//    object Screen4 : Routes("screen4/{name}/{age}") {
//        fun createRoute(name: String, age: Int) = "screen4/$name/$age"
//    }
//
//    object Screen5 : Routes("screen5?name={name}") {
//        fun createRoute(name: String) = "screen5?name=$name"
//    }

}
