#!/system/bin/sh
echo OK
while read cmd rest
do
    case $cmd in
        SETDESC)
            DESC=$rest
            echo OK
        ;;
        SETPROMPT)
            PROMPT=$rest
            echo OK
        ;;
        SETOK)
            OK=$rest
            echo OK
        ;;
        SETERROR)
            ERROR=$rest
            echo OK
        ;;
        GETPIN)

            echo "D "
            echo OK
        ;;
        OPTION)
            echo OK
        ;;
        GETINFO)
            case $rest in
                pid*)
                    echo D $$
                    echo OK
                ;;
            esac
        ;;
        BYE)
            echo OK
            exit
        ;;
        *)
            echo OK
        ;;
    esac
done

