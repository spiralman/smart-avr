metadata {
  definition (name: "Smart AVR Device", namespace: "spiralman", author: "Thomas Stephens") {
    capability "Switch"
  }

  preferences {
    input "receiverIp", "string", title: "Receiver IP address",
               description: "IP Address of Receiver (not proxy)",
               required: true, displayDuringSetup: true
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles {
    standardTile("switch", "device.switch", width: 1, height: 1,
                 canChangeIcon: true) {
      state "off", label: '${name}', action: "switch.on",
                                       icon: "st.switches.switch.off",
                                       backgroundColor: '#ffffff'
      state "on", label: '${name}', action: "switch.off",
                                       icon: "st.switches.switch.on",
                                       backgroundColor: '#79b821'
    }

    main "switch"
    details "switch"
  }
}

def getProxyIp() {
  return getDataValue("ip")
}

def getProxyPort() {
  return getDataValue("port")
}

// parse events into attributes
def parse(String description) {
  log.debug "Parsing '${description}'"

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

def on() {
  httpGet([uri: "http://${proxyIp}:${proxyPort}/avr/command",
           query: [cmd: "PWON"]]) {
    resp ->
      log.debug "Got response: ${resp.body}"
  }
}

def off() {
}
