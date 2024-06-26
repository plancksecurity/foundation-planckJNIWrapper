#pragma once

#include <pEp/passphrase_cache.hh>
#include "passphrase_entry.hh"


namespace pEp {
namespace JNIAdapter {

passphrase_entry passphraseRequiredCallback(const PEP_STATUS status);
template<typename... A> PEP_STATUS passphraseWrap(PEP_STATUS f(PEP_SESSION, A...), PEP_SESSION session, A... a);

}
}

#include "passphrase_callback.hxx"