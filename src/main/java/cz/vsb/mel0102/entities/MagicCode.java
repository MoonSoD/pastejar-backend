package cz.vsb.mel0102.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MagicCode implements IEntity {
    public long id;
    public String code;
    public long pasteId;
    boolean mutable;
}
