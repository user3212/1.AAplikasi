import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We want to add scale(0.95f) or scale(0.9f) to the Box to make it slimmer.
    old_box = """        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))"""
                
    new_box = """        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .scale(0.95f)
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))"""

    content = content.replace(old_box, new_box)
    
    # We should add the import for scale if it's not there, or use fully qualified name
    old_box_fq = """        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .androidx.compose.ui.draw.scale(0.9f)
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))"""
                
    content = content.replace(new_box, old_box_fq)
    
    # Let's just use fully qualified name directly:
    new_box_fq = """        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .androidx.compose.ui.draw.scale(0.9f)
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))"""
                
    content = content.replace(old_box, new_box_fq)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/components/CommonComponents.kt')
