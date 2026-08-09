import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove the Professional Circular Photo Projection block
    photo_block = r"""                Spacer\(modifier = Modifier.width\(16.dp\)\)\s*// Professional Circular Photo Projection\s*Box\([\s\S]*?\}\s*\}"""
    
    # We will just replace it with an empty string, carefully using regex since it spans multiple lines.
    # Actually, string replacement by finding start and end is safer.
    
    start_str = '                Spacer(modifier = Modifier.width(16.dp))\n                   \n                // Professional Circular Photo Projection\n                Box('
    
    # Find start
    idx_start = content.find(start_str)
    if idx_start != -1:
        # It's an empty string replacing until the closing of Box
        # We know Box closes after:
        #                         )
        #                     }
        #                 }
        # Let's find the `                }` corresponding to the Box.
        idx_end = content.find('                }\n            }', idx_start)
        
        # Wait, the structure is:
        #                 Box(
        #                     ...
        #                 ) {
        #                     if (bitmap != null) {
        #                         ...
        #                     } else {
        #                         ...
        #                     }
        #                 }
        #             }
        #         }
        
        idx_end = content.find('                }\n            }\n        }\n', idx_start)
        if idx_end != -1:
            # We replace from idx_start to the end of the Box which is just before `            }\n        }\n`
            content = content[:idx_start] + content[idx_end:]

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
