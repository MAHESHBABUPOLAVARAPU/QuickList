package uk.ac.tees.mad.safeher.presentation.navigation

import kotlinx.serialization.Serializable


sealed class Routes {

    @Serializable
    data object AuthScreen

    @Serializable
    data object SingInScreen

    @Serializable
    data object LoginScreen

    @Serializable
    data object HomeScreen

    @Serializable
    data object ActivityScreen
    @Serializable
    data object ProfileScreen

    @Serializable
    data object  AddEditScreen

}