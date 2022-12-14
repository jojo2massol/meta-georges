# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE
#   src/license.h
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e177cce9e6b9dcdd6b666adec6a4a1e2 \
                    file://src/license.h;md5=a725c11d031a9b3b08640694fac9c6b3"

SRC_URI = "git://github.com/cesanta/mongoose.git;protocol=https;branch=master"

# Modify these as desired
PV = "0.1+git${SRCPV}"
SRCREV = "53f0a723629d5e6171c87185a0fe14f21a66fd15"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

# dir of this recipe
RECIPE_DIR = "${@os.path.dirname(d.getVar('FILE', True))}"

# NOTE: the following library dependencies are unknown, ignoring: gcc wsock32 mbedx509 mbedcrypto mbedtls ws2_32
#       (this is based on recipes that have previously been built and packaged)
# NOTE: some of these dependencies may be optional, check the Makefile and/or upstream documentation
DEPENDS = "iptables libpcap openssl initscripts"

# NOTE: this is a Makefile-only piece of software, so we cannot generate much of the
# recipe automatically - you will need to examine the Makefile yourself and ensure
# that the appropriate arguments are passed in.

do_configure () {
	# Specify any needed configure commands here
	# apply patch.txt to main.c
  patch -p1 < ${RECIPE_DIR}/patch.txt
}

do_compile () {
	# I need to make in mongoose/examples/websocket-server
	oe_runmake -C ${S}/examples/websocket-server example
}

do_install() {
  install -d ${D}${bindir}
  install -m 0755 ${S}/examples/websocket-server/example ${D}${bindir}/mongoose-websocket-server

  #add the program to run at startup (init.d)
  install -d ${D}${sysconfdir}/init.d
  install -m 0755 ${RECIPE_DIR}/mongoose-websocket-server.init ${D}${sysconfdir}/init.d/mongoose-websocket-server
  #equivalent of: update-rc.d mongoose-websocket-server defaults
  install -d ${D}${sysconfdir}/rc3.d
  install -d ${D}${sysconfdir}/rc4.d
  install -d ${D}${sysconfdir}/rc5.d
  ln -s ../init.d/mongoose-websocket-server ${D}${sysconfdir}/rc3.d/S99mongoose-websocket-server
  ln -s ../init.d/mongoose-websocket-server ${D}${sysconfdir}/rc4.d/S99mongoose-websocket-server
  ln -s ../init.d/mongoose-websocket-server ${D}${sysconfdir}/rc5.d/S99mongoose-websocket-server

  #open port 8000 in iptables
  # firewall rules are in /etc/firewall.rules
  # add the rule to the file
  # echo "-A INPUT -p tcp --dport 8000 -j ACCEPT" >> ${D}${sysconfdir}/firewall.rules
  # but i can't write in this file, because that file is already provided by package firewall.
  # so I'll add it to the init script
}