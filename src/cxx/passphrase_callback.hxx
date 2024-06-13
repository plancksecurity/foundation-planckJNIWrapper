#pragma once

#include "passphrase_callback.hh"

namespace pEp {
namespace JNIAdapter {

template<typename... A> PEP_STATUS passphraseWrap(PEP_STATUS f(PEP_SESSION, A...), PEP_SESSION session, A... a) {
    pEpLog("cached passphrase mode");
    bool retryAgain = false;
    int maxRetries = 3;
    int retryCount = 0;
    PEP_STATUS status;
    do {
        // the actual target function
        pEpLog("calling passphrase_cache.api from basic_api");
        status = passphrase_cache.api(f, session, a...);
        pEpLog("PEP_STATUS:" << status);
        if (status == PEP_PASSPHRASE_REQUIRED ||
            status == PEP_WRONG_PASSPHRASE ||
            status == PEP_PASSPHRASE_FOR_NEW_KEYS_REQUIRED)
        {
            pEpLog("none of the cached passphrases worked");
            if (retryCount < maxRetries) {
                // call the app
                std::string email = status == PEP_PASSPHRASE_FOR_NEW_KEYS_REQUIRED ? PassphraseCache::PASSPHRASE_FOR_NEW_KEYS_ENTRY : "android04@planck.dev";
                PassphraseCache::cache_entry entry = passphraseRequiredCallback(status, email.c_str()); // need to get the email from somewhere, probably core
                pEpLog("callback returned, config_passphrase() with new passphrase");
                PEP_STATUS inner_status;
                if (status == PEP_PASSPHRASE_FOR_NEW_KEYS_REQUIRED) {
                    if (entry.email == PassphraseCache::PASSPHRASE_FOR_NEW_KEYS_ENTRY) {
                        inner_status = ::config_passphrase_for_new_keys(
                                session, true,
                                passphrase_cache.add_passphrase_for_new_keys(entry)); // this one can stay as it is in core
                    } // else some warning
                } else {
                    inner_status = ::config_passphrase(session, passphrase_cache.add(entry).passphrase.c_str()); // needs to be changed in core
                }
                if (inner_status == PEP_OUT_OF_MEMORY) {
                    return inner_status;
                }
                retryAgain = true;
                retryCount++;
            } else {
                pEpLog("max retries reached:" << maxRetries);
                retryAgain = false;
            }
        } else {
            retryAgain = false;
        }
    } while (retryAgain);
    return status;
}
}
}