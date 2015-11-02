#!/bin/bash

read -p "COMMENT: " COMMENT &&
    read -p "HOST: " HOST &&
    read -p "HOSTNAME: " HOSTNAME &&
    read -p "USER: " USER &&
    mkdir --parents ${HOME}/.ssh/keys &&
    chmod 0700 ${HOME}/.ssh/keys &&
    mkdir --parents ${HOME}/.ssh/control &&
    chmod 0700 ${HOME}/.ssh/control &&
    chmod 0700 ${HOME}/.ssh &&
    KEY_FILE=$(mktemp ${HOME}/.ssh/keys/XXXXXXXX_id_rsa) &&
    rm ${KEY_FILE} &&
    CTRL_FILE=$(mktemp ${HOME}/.ssh/control/XXXXXXXX_%h_%p_%r_ctrl_path) &&
    rm ${CTRL_FILE} &&
    ssh-keygen -f ${KEY_FILE} -C "${COMMENT}" &&
    (cat >> ${HOME}/.ssh/config <<EOF
Host ${HOST}
Host ${HOSTNAME}
User ${USER}
IdentityFile ${KEY_FILE}
ControlMaster auto
ControlPath ${CTRL_FILE}
EOF
    ) &&
    chmod 0600 ~/.ssh/config &&
    true
