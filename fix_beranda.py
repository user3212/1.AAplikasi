import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Fix the extra brace closing the Column early
    # The snippet is:
    #                 }
    #                    
    #                 }
    #             }
    #
    #             // Digital Clock and Date Container
    
    snippet = """                }
                   
                }
            }

            // Digital Clock and Date Container"""
            
    fixed_snippet = """                }
            }

            // Digital Clock and Date Container"""
            
    content = content.replace(snippet, fixed_snippet)
    
    # 2. We also need to make sure the end of HeroBerandaBanner has enough braces.
    # We removed one closing brace early, so we need one more at the end of HeroBerandaBanner.
    # Where does HeroBerandaBanner end? Right before `@Composable\nfun RiayahEmblemLogo`
    
    end_snippet = """        }
    }
}

@Composable
fun RiayahEmblemLogo"""

    fixed_end_snippet = """            }
        }
    }
}

@Composable
fun RiayahEmblemLogo"""

    if end_snippet in content:
        content = content.replace(end_snippet, fixed_end_snippet)
        
    # 3. Add the missing brace at the end of RiayahEmblemLogo
    # Since fix_braces.py removed one from the end of the file:
    if not content.strip().endswith("}"):
        content = content.rstrip() + "\n}\n"
    elif content.strip().endswith("    }"):
        # maybe it is `    }` instead of `}`
        # let's just append `\n}\n` because a top level function must close with `}`
        content = content.rstrip() + "\n}\n"

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/BerandaScreen.kt')
