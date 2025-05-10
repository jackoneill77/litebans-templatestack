# LiteBans-TemplateStack

**LiteBans-TemplateStack** is an extension to the [LiteBans](https://www.spigotmc.org/resources/litebans.3715/) plugin made by Ruan. It allows combining ladder templates from multiple punishment types (ban, mute, kick, warn) into one simple template.
This allows using one single command to be used to issuing different punishments depending on where the player on the punishment ladder is.

A **TemplateStack** is defined as a group of _at least_ one punishment template. Templates of all four types (warn, kick, mute, ban) can be combined. 

## Configuration

| Key                        | Description                                                                                                                 | required | default if not set         | 
|----------------------------|-----------------------------------------------------------------------------------------------------------------------------|----------|----------------------------|
| `debug`                    | enables detailed logging output in console, default=false                                                                   | no       | `false`                    |
| `command_alias`            | list of command aliases used for the punish-command                                                                         | no       | `['banmutekickwarn']`      |
| `defaults.expiration_days` | default value for the number of days until a punishment expires and <br/>is not considered for ladder progression anymore * | no       | `30`                       |
| `defaults.punishments`     | default value for the number of punishments in a ladder type *                                                              | no       | `1`                        |
| `template_stacks.<key>`    | name of the template stack - will be used for command auto-completion                                                       | yes      | -                          |
| __ `<key>.enabled`         | boolean flag, if false, template stack will not be loaded                                                                   | no       | `true`                     |
| __ `<key>.expiration_days` | number of days until a punishment expires <br/>and is not considered for ladder progress anymore                            | no       | `defaults.expiration_days` |
| __ `<key>.templates`       | List of 1-4 punishment types                                                                                                | yes      | -                          |
| ____ `.<type>`             | at least one type of `ban\|mute\|kick\|warn` is required                                                                    | yes      | -                          |
| ____ `.<type>.template`    | name of the template in `LiteBans/templates.yml` in the right template-section                                              | no       | -                          |
| ____ `.<type>.enabled`     | boolean flag, if false, template type will not be used in the template stack                                                | no       | `true`                     |
| ____ `.<type>.punishments` | number of punishments in a ladder type, only used if the template type is not the highest priority \*\*                     | no       | `defaults.punishments`     |

\* default values will be used if the attribute is not set in the template stack.

\*\* order of templates is fixed and always `warn -> kick -> mute -> ban`.  

## Example Use-Case

Let's assume, you want to have a punishment for Spam, that does the following:

| Offense | Punishment | Duration |
|---------|------------|----------|
| 1       | Warn       | -        |
| 2       | Kick       | -        |
| 3       | Kick       | -        |
| 4       | Mute       | 1h       |
| 5       | Mute       | 2h       |
| 6       | Mute       | 3h       |
| 7       | Mute       | 3h       |
| 8       | Ban        | 1h       |
| 9       | Ban        | 2h       |
| 10+     | Ban        | 3h       |

In LiteBans, this can normally only be achieved with defining a template in one category type and 
then using the `actions` attribute to define actions outside the default-action of the template. With this, you lose 
the ability to have meaningful auto-complete suggestions when using the LiteBans commands like `/ban <player> template`.

LiteBans-TemplateStack introduces a new command that combines multiple punishment types ladders into one while retain everything else that LiteBans offers.


### Config

To get the multi-type ladder as described above, you can define the follow Spam template in `template_stacks` in the plugin's `config.yml` file.
```
template_stacks:
  Spam:
    enabled: true
    expiration_days: 90
    templates:
      ban:
        template: 'Spam'
        enabled: true
      mute:
        template: 'Spam'
        enabled: true
        punishments: 4
      kick:
        template: 'Spam'
        enabled: true
        punishments: 2
      warn:
        template: 'Spam'
        enabled: true
        punishments: 1
```

### LiteBans Template

In the LiteBans `templates.yml` you define the references templates and configure them with the desired message formatting, durations, etc.

The `template`-values shown above have to match with a template key inside the corresponding `type-templates` section. 
For simplicity, in this example they are all named `Spam`, but they do not have to use the same name across punishment types.

```
ban-templates:
  Spam:
    reason: 'Spam'
    message: 'You have been banned for spamming!'
    permission: litebans.group.spam
    ladder:
      third:
        duration: 3h
      second:
        duration: 2h
      first:
        duration: 1h
    expire_ladder: 90d
    ip_template: false
    
mute-templates:
  Spam:
    reason: 'Spam'
    message: 'You have been muted for spamming!'
    permission: litebans.group.spam
    ladder:
      third:
        duration: 3h
      second:
        duration: 2h
      first:
        duration: 1h
    expire_ladder: 30d
    ip_template: false
    
warn-templates:
  Spam:
    reason: 'Spam'
    message: 'You have been warned for spamming!'
    permission: litebans.group.spam
    ladder:
      second:
        message: '&4You should really stop. &eThis warn should not happen.. '
      first:
        message: '&cPlease stop spamming!\nThis is your first and only warning. If you do this again, you will be muted.'
    expire_ladder: 30d
    ip_template: false
    
kick-templates:
  Spam:
    reason: 'Spam'
    message: 'You have been kicked for spamming!'
    permission: litebans.group.spam
    ladder:
      second:
        message: '&4You should really stop.'
      first:
        message: '&cPlease stop spamming!\nIf you do this again, you will be muted.'
    expire_ladder: 30d
    ip_template: false

```

