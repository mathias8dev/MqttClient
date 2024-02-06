package com.mathias8dev.mqttclient.ui.screens.configuration

import io.github.mathias8dev.yup.Yup

val topicValidationConstraints = Yup.ValidationConstraints {
    required {
        errorMessage = "Ce champ est requis"
    }
    minLength {
        length = 4
        errorMessage = "Le topic doit avoir une longueur minimale de 4"
    }
    maxLength {
        length = 20
        errorMessage = "Le topic ne doit pas dépasser plus de 20 caractères"
    }

    regex {
        regex = Regex("^[a-zA-Z0-9_\\-().|{}]+$")
        errorMessage = "Ne peut pas contenir d'espace mais alpha numériques avec [-_|{}()]"
    }
}

val portValidationConstraints = Yup.ValidationConstraints {
    required {
        errorMessage = "Ce champ est requis"
    }
    integer {
        minValue = 1025
        maxValue = 65535
        errorMessage = "Le port est un entier compris entre 1025 et 65535"
    }
}

val urlValidationConstraints = Yup.ValidationConstraints {
    required {
        errorMessage = "Ce champ est requis"
    }
    regex {
        regex = Regex("^[a-zA-Z0-9.\\-_]+$")
        errorMessage =
            "L'url est invalide. Elle ne doit pas contenir de schéma. Exemple: mathias8dev.com"
    }
}