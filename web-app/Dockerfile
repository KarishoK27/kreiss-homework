FROM python:3.8
ENV PYTHONUNBUFFERED 1
RUN mkdir /web_app_dir
WORKDIR /web_app_dir
ADD requirements.txt /web_app_dir/
RUN pip install --upgrade pip && pip install -r requirements.txt
ADD . /web_app_dir/