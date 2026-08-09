import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Collect profilBase64 state in MainActivity if not already collected
    # Let's see where to inject it. We can just add it near `val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()`
    
    state_injection = "    val profilBase64 by viewModel.profilBase64.collectAsState()\n"
    if "val profilBase64 by viewModel.profilBase64.collectAsState()" not in content:
        content = content.replace("val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()",
                                  "val activeCustomNav by viewModel.activeCustomSubjectNav.collectAsState()\n" + state_injection)
                                  
    # Update MobileHeader call
    old_call = """                    MobileHeader(
                        currentTitle = headerTitle,
                        onOpenDrawer = { viewModel.toggleDrawer(!isDrawerOpen) },
                        onProfileClick = { showProfileDialog = true },
                        isProfileVerified = isProfileVerified
                    ) {"""
    new_call = """                    MobileHeader(
                        currentTitle = headerTitle,
                        onOpenDrawer = { viewModel.toggleDrawer(!isDrawerOpen) },
                        onProfileClick = { showProfileDialog = true },
                        isProfileVerified = isProfileVerified,
                        profilBase64 = profilBase64
                    ) {"""
    content = content.replace(old_call, new_call)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/MainActivity.kt')
