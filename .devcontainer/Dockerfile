FROM mcr.microsoft.com/devcontainers/python:3.9

# Install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    xvfb \
    libxi6 \
    libgconf-2-4 \
    libnss3-dev \
    libxss1 \
    libappindicator1 \
    fonts-liberation \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm1 \
    libasound2 \
    libdpkg-perl \
    libatomic1 \
    ca-certificates \
    curl \
    gnupg \
    python3-venv \
    awscli

# Install pipx and biobricks
RUN python3 -m pip install --user pipx \
    && python3 -m pipx ensurepath

# Create /mnt/biobricks directory and set permissions
RUN mkdir -p /mnt/biobricks/biobricks-ai \
    && chown -R vscode:vscode /mnt/biobricks

# Add pipx binaries to the PATH for all users
RUN echo 'export PATH="$PATH:$HOME/.local/bin"' >> /etc/bash.bashrc \
    && echo 'export PATH="$PATH:$HOME/.local/bin"' >> /home/vscode/.bashrc

# Switch to vscode user to perform user-specific installations
USER vscode

# Install Python dependencies
COPY .devcontainer/requirements.txt /tmp/requirements.txt
RUN pip install -r /tmp/requirements.txt

# Accept build argument for BIOBRICKS_TOKEN & set to the default value if it is not given.
ARG BIOBRICKS_TOKEN
ENV DEFAULT_TOKEN=VQF6Q2U-NKktZ31ioVYa9w
ENV BIOBRICKS_TOKEN=${BIOBRICKS_TOKEN:-${DEFAULT_TOKEN}}
RUN if [ ${#BIOBRICKS_TOKEN} -lt 5 ]; then export BIOBRICKS_TOKEN=$DEFAULT_TOKEN; fi

# Install biobricks and configure it
RUN /bin/bash -c 'source /etc/bash.bashrc && pipx install biobricks && biobricks version' \
    && /bin/bash -c 'if [ -z "$BIOBRICKS_TOKEN" ] || [ ${#BIOBRICKS_TOKEN} -lt 5 ]; then echo "BIOBRICKS_TOKEN is not set or is too short (less than 5 characters)"; exit 1; fi' \
    && /bin/bash -c 'source /etc/bash.bashrc && biobricks configure --bblib=/mnt/biobricks --token=${BIOBRICKS_TOKEN} --interactive=False'

# Switch back to root user to complete setup
USER root
