metadata {
  definition (name: "Smart AVR Device", namespace: "spiralman", author: "Thomas Stephens") {
    capability "Switch"
    capability "Refresh"
    capability "Media Controller"

    command "sourceUp"
    command "sourceDown"
  }

  preferences {
    input "receiverIp", "string", title: "Receiver IP address",
               description: "IP Address of Receiver (not proxy)",
               required: true, displayDuringSetup: true
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    multiAttributeTile(name: "dashboard", type: "generic", width: 6, height: 4) {
      tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: '#ffffff'
        attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: '#79b821'
      }

      tileAttribute("device.currentActivity", key: "SECONDARY_CONTROL") {
        attributeState("default", label: 'Source: ${currentValue}')
      }

      tileAttribute("device.currentActivity", key: "VALUE_CONTROL") {
        attributeState "VALUE_UP", label: "", action: "sourceUp"
        attributeState "VALUE_DOWN", label: "", action: "sourceDown"
      }
    }

    standardTile("refresh", "device.switch", inactiveLabel: false,
                 decoration: "flat", width: 1, height: 1) {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }

    main "dashboard"
    details(["dashboard", "refresh"])
  }
}

def getProxyIp() {
  getDataValue("ip")
}

def getProxyPort() {
  return getDataValue("port")
}

def _parsePW(line) {
  def avrState = line.substring(2)
  def switchState

  if (avrState == 'ON') {
    switchState = 'on'
  }
  else if (avrState == 'STANDBY') {
    switchState = 'off'
  }

  log.debug "The receiver is ${switchState}"
  return createEvent(name: 'switch', value: switchState)
}

def _parseSI(line) {
  def inputState = line.substring(2)
  log.debug "The receiver is on input ${inputState}"
  return createEvent(name: 'currentActivity', value: inputState)
}

// parse events into attributes
def parse(String description) {
  def msg = parseLanMessage(description)
  def events = []

  msg.body.eachLine { line ->
    if (line.startsWith('PW')) {
      events << _parsePW(line)
    }
    else if (line.startsWith('SI')) {
      events << _parseSI(line)
    }
    else {
      log.debug "Unknown line: ${line}"
    }
  }

  return events
}

def sync(ip, port) {
  def existingIp = getDataValue("ip")
  def existingPort = getDataValue("port")
  if (ip && ip != existingIp) {
    updateDataValue("ip", ip)
  }
  if (port && port != existingPort) {
    updateDataValue("port", port)
  }
}

def _avrCommand(command) {
  def result = new physicalgraph.device.HubAction(method: "GET",
                                                  path: "/avr/command",
                                                  headers: [
                                                    HOST: getHostAddress()
                                                  ],
                                                  query: [cmd: command]
                                                 )
  return result
}

def on() {
  return _avrCommand("PWON")
}

def off() {
  return _avrCommand("PWSTANDBY")
}

def refresh() {
  getAllActivities()
  return [_avrCommand("PW?"),
          getCurrentActivity()]
}

def _sourceIndex() {
  def currentSource = device.currentValue("currentActivity")
  def sources = device.currentValue("activities")
  sources = sources.substring(1, sources.size() - 1).split(', ')

  log.debug "Currently: ${currentSource} of ${sources}"

  def sourceIndex = sources.indexOf(currentSource)
  log.debug "Source at index ${sourceIndex}"
  return sourceIndex
}

def sourceUp() {
  log.debug "Source up"
  def sources = device.currentValue("activities")
  def curIndex = _sourceIndex()

  log.debug "Switching from source ${curIndex}"

  if (curIndex == 0) {
    curIndex = sources.size() - 1
  }
  else {
    curIndex -= 1
  }

  log.debug "to source ${curIndex}"

  return startActivity(sources.getAt(curIndex))
}

def sourceDown() {
  log.debug "Source down"
  def sources = device.currentValue("activities")
  def curIndex = _sourceIndex()

  log.debug "Switching from source ${curIndex}"

  if (curIndex == sources.size() - 1) {
    curIndex = 0
  }
  else {
    curIndex += 1
  }

  log.debug "to source ${curIndex}"

  return startActivity(sources.getAt(curIndex))
}

def startActivity(activity) {
  log.debug "Switching to source ${activity}"
  return _avrCommand("SI" + activity)
}

def getAllActivities() {
  sendEvent(name: 'activities', value:
            [
              "CD",
              "TUNER",
              "DVD",
              "BD",
              "TV",
              "SAT/CBL",
              "GAME",
              "GAME2",
              "V.AUX",
              "DOCK",
              "IPOD",
              "NET/USB",
              "RHAPSODY",
              "NAPSTER",
              "PANDORA",
              "LASTFM",
              "FLICKR",
              "FAVORITES",
              "IRADIO",
              "SERVER",
              "USB/IPOD",
              "USB",
              "IPD",
              "IRP",
              "FVP"
            ])
}

def getCurrentActivity() {
  return _avrCommand("SI?")
}

// Just copy pasted from SmartThings docs :-(
// gets the address of the device
private getHostAddress() {
  def ip = getDataValue("ip")
  def port = getDataValue("port")

  if (!ip) {
    def parts = device.deviceNetworkId.split(":")
    if (parts.length == 2) {
      ip = parts[0]
      port = parts[1]
    } else {
      log.warn "Can't figure out ip and port for device: ${device.id}"
    }
  }

  log.debug "Using IP: $ip and port: $port for device: ${device.id}"
  return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
  return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
  return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
