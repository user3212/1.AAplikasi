with open("app/src/main/java/com/example/ui/components/CommonComponents.kt", "r") as f:
    content = f.read()

content = content.replace("""    placeholder: String = "",
    focusAccentColor: Color = MaterialTheme.colorScheme.primary,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    testTag: String = ""
""", """    placeholder: String = "",
    focusAccentColor: Color = MaterialTheme.colorScheme.primary,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    testTag: String = ""
""")

with open("app/src/main/java/com/example/ui/components/CommonComponents.kt", "w") as f:
    f.write(content)
