SUMMARY = "My personnal image"
HOMEPAGE = "https://georges.ml"

require images/console-image.bb

IMAGE_INSTALL += " \
    nano \
    htop \
    neofetch \
    mongoose \
"

export IMAGE_BASENAME = "georges-image"
