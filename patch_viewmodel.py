import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Add AlarmScheduler import
    if 'import com.example.ui.util.AlarmScheduler' not in content:
        content = content.replace('import com.example.ui.util.SoundHelper', 'import com.example.ui.util.SoundHelper\nimport com.example.ui.util.AlarmScheduler')

    # Update updateJadwalConfig
    config_target = r'''fun updateJadwalConfig\(sound: String, repetition: Int\) \{
\s*_jadwalSound.value = sound
\s*_jadwalRepetition.value = repetition
\s*prefs.edit\(\)
\s*.putString\("jadwal_sound", sound\)
\s*.putInt\("jadwal_repetition", repetition\)
\s*.apply\(\)
\s*\}'''
    
    config_replacement = '''fun updateJadwalConfig(sound: String, repetition: Int) {
        _jadwalSound.value = sound
        _jadwalRepetition.value = repetition
        prefs.edit()
            .putString("jadwal_sound", sound)
            .putInt("jadwal_repetition", repetition)
            .apply()
        AlarmScheduler.scheduleAlarms(getApplication(), _jadwalList.value, sound, repetition)
    }'''
    
    content = re.sub(config_target, config_replacement, content)

    # Update saveJadwal
    save_target = r'''prefs.edit\(\).putString\("jadwal_data", jsonArray.toString\(\)\).apply\(\)
\s*\}'''

    save_replacement = '''prefs.edit().putString("jadwal_data", jsonArray.toString()).apply()
        AlarmScheduler.scheduleAlarms(getApplication(), list, _jadwalSound.value, _jadwalRepetition.value)
    }'''

    content = re.sub(save_target, save_replacement, content)

    # Call it in loadJadwal if it exists
    load_target = r'''_jadwalList.value = list
\s*\} catch \(e: Exception\) \{'''

    load_replacement = '''_jadwalList.value = list
            AlarmScheduler.scheduleAlarms(getApplication(), list, _jadwalSound.value, _jadwalRepetition.value)
        } catch (e: Exception) {'''

    content = re.sub(load_target, load_replacement, content)

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/viewmodel/PesantrenViewModel.kt')
