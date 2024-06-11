#pragma once

#include <pEp/passphrase_cache.hh>


namespace pEp {
namespace JNIAdapter {

PassphraseCache::cache_entry passphraseRequiredCallback(const PEP_STATUS status, const char* email);
template<typename... A> PEP_STATUS passphraseWrap(PEP_STATUS f(PEP_SESSION, A...), PEP_SESSION session, A... a);

}
}

#include "passphrase_callback.hxx"