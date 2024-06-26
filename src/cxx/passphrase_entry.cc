#include "passphrase_entry.hh"

namespace pEp {
    namespace JNIAdapter {
        passphrase_entry::passphrase_entry(const std::string email, const std::string passphrase) :
                email { email, 0, passphrase_entry::max_len },
                passphrase{ passphrase, 0, passphrase_entry::max_len }
        {
        }
    }
}
