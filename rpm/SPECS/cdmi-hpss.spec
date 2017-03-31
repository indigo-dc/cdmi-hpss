%define __jar_repack 	%{nil}
%define _tmppath	%{_topdir}/tmp
%define buildroot	%{_topdir}/build-rpm-root

%define name            cdmi-hpss
%define jarversion      1.2

Name:		%{name}
Version:	%{jarversion}
Release:	1%{?dist}
Summary:	Java Service Provider Interface (SPI) module.
Group:		Applications/Web
License:	apache2
URL:		https://github.com/indigo-dc/cdmi-hpss

Requires:	jre >= 1.8, cdmi-server

%description
HPSS back-end module for the INDIGO DataCloud CDMI server.
Java Service Provider Interface (SPI) module.

%prep

%build

%install
mkdir -p %{buildroot}/var/lib/%{name}
mkdir -p %{buildroot}/usr/lib/cdmi-server/plugins
mkdir -p %{buildroot}/etc/cdmi-server/plugins
cp %{_topdir}/SOURCES/%{name}-%{jarversion}-jar-with-dependencies.jar %{buildroot}/var/lib/%{name}
cp %{_topdir}/SOURCES/%{name}-%{jarversion}-jar-with-dependencies.jar %{buildroot}/usr/lib/cdmi-server/plugins
cp %{_topdir}/SOURCES/capabilities.json %{buildroot}/etc/cdmi-server/plugins
cp %{_topdir}/SOURCES/configuration.json %{buildroot}/etc/cdmi-server/plugins

%files
/var/lib/%{name}/%{name}-%{jarversion}-jar-with-dependencies.jar
/usr/lib/cdmi-server/plugins/%{name}-%{jarversion}-jar-with-dependencies.jar
/etc/cdmi-server/plugins/capabilities.json
/etc/cdmi-server/plugins/configuration.json

%changelog

%post
/usr/bin/id -u %{user} > /dev/null 2>&1
if [ $? -eq 1 ]; then
  adduser --system --user-group %{user}
fi

chown -R %{user}:%{user} /var/lib/%{name}
chown -R %{user}:%{user} /usr/lib/cdmi-server/plugins
chown -R %{user}:%{user} /etc/cdmi-server/plugins

