[![IMG-6450.png](https://i.postimg.cc/RCYFGg87/IMG-6450.png)](https://postimg.cc/c6Q0LBdC)
<img 
  src="https://img.shields.io/badge/version-1.0.0-yellow?style=flat-square" 
  alt="Version 1.0.0" 
  style="width: auto; height: 28px;">
<a href="https://github.com/karepanov35/-Nukkit-Lumi-AuraRandomTP/blob/main/LICENSE" target="_blank">
<img 
  src="https://img.shields.io/badge/License-GPL-orange?style=flat-square" 
  alt="GPL License" 
  style="height: 28px; width: auto;">
  </a>
  <a href="https://github.com/karepanov35/-Nukkit-Lumi-AuraRandomTP/blob/main/LICENSE" target="_blank">
  <img src="https://img.shields.io/badge/24serv_Page-click_me-blue?style=for-the-badge" alt="24serv Page" style="height: 28px;">
  </a>
  ________________

AuraRandomTP is a plugin for Bedrock Edition designed to create random player teleportation using flexible configurations. Our plugin works on the [Lumi](https://github.com/KoshakMineDEV/Lumi) core and [NUKKIT-MOT](https://github.com/MemoriesOfTime/Nukkit-MOT). Our plugin has good functionality and flexible customization!

## 🛠 Installation

1. **Download** the latest version:  
   *(Replace with your actual GitHub release URL)*

2. **Place** the `AuraRandomTP-1.0.0.jar` file into your server's `plugins` folder.

3. **Restart** the server (or use `/reload`).

4. **Configure** the plugin (auto-generated at `plugins/AuraRandomTP/config.yml`):

## ⚙ Configuration
```yaml
# Настройки телепортации
min-x: -1000
max-x: 1000
min-z: -1000
max-z: 1000
max-attempts: 100 # Количество успешных попыток, лучше не трогать
min-y: 1
max-y: 256
teleport-delay: 5 # Задержка на телепорт 

# Сообщения
messages:
  only-players: "§6❱ §cЭта команда только для игроков!"
  teleport-success: "§6❱ §fВы успешно телепортировались на координаты: §6{x}, {y}, {z}."
  title-teleport: "§aТелепортация!"
  subtitle-teleport: "§6❱ §fТелепортация на координаты: §6{x}, {y}, {z}."
  title-delay: "§aТелепортация"
  subtitle-delay: "§fТелепортация через {seconds} сек..."
```


🔓 The plugin is distributed under the open GPL-3.0 license. Any resale, modification, or distribution must comply with the terms of this license.




