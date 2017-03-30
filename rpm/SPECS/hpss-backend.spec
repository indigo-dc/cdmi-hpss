%define __jar_repack 	%{nil}
%define _tmppath	%{_topdir}/tmp
%define buildroot	%{_topdir}/build-rpm-root

%define name            cdmi-hpss
%define jarversion      0.1

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
cp %{_topdir}/SOURCES/%{name}-%{jarversion}-jar-with-dependencies.jar %{buildroot}/var/lib/%{name}

%files
/var/lib/%{name}/%{name}-%{jarversion}-jar-with-dependencies.jar

%changelog

%post
if [ -f /var/lib/$NAME/$NAME-$VERSION-jar-with-dependencies.jar ]; then
    for path in `find / -path "*/jre/lib/ext" 2>/dev/null`; do
        cp /var/lib/$NAME/$NAME-$VERSION-jar-with-dependencies.jar $path
    done
fi
