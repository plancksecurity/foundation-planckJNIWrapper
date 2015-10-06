#!/system/bin/sh

exit_with_log() {
    echo Pipe kill $$ >> /data/data/com.pep.pepjniaaractivity/files/pinentry.log
    exit
}
trap "exit_with_log" 13

echo Start $$ >> /data/data/com.pep.pepjniaaractivity/files/pinentry.log
echo OK
while read cmd rest
do
    echo $cmd $rest >> /data/data/com.pep.pepjniaaractivity/files/pinentry.log
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
                    echo D $$ >> /data/data/com.pep.pepjniaaractivity/files/pinentry.log
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

